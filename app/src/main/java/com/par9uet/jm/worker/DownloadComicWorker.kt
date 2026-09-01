package com.par9uet.jm.worker

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.drawable.toBitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.par9uet.jm.dir.getDownloadCacheDir
import com.par9uet.jm.controller.DownloadConcurrencyController
import com.par9uet.jm.database.dao.DownloadComicDao
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.database.model.UpdateComicProgress
import com.par9uet.jm.database.model.UpdateComicStatus
import com.par9uet.jm.database.model.UpdateWhenComplete
import com.par9uet.jm.dir.getDownloadCoverDataDir
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.utils.tryCreateDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadComicWorker(
    private val appContext: Context,
    params: WorkerParameters,
    private val downloadComicDao: DownloadComicDao,
    private val remoteSettingManager: RemoteSettingManager,
    private val localSettingManager: LocalSettingManager,
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager,
    private val downloadConcurrencyController: DownloadConcurrencyController
) : CoroutineWorker(appContext, params) {

    private val loader = ImageLoader(appContext)
    private var coverDir = getDownloadCoverDataDir(appContext)

    override suspend fun doWork(): Result {
        val comicId = inputData.getInt("comicId", -1)
        if (comicId == -1) {
            return Result.failure()
        }
        downloadConcurrencyController.acquire()
        return try {
            val downloadComic = downloadComicDao.getOne(comicId)
            downloadComicDao.updateStatus(
                UpdateComicStatus(
                    downloadComic.id,
                    "downloading"
                )
            )
            downloadCover(comicId)
            val picList =
                downloadPicList(comicId, localSettingManager.localSettingState.value.shunt)
            val zipFile = zipPicPathList(downloadComic, picList)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, zipFile.name)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                // 保存到 Download/jm-mobile 目录下
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DOWNLOADS}/jm-mobile"
                )
            }

            val uri =
                appContext.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
            uri?.let {
                appContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    zipFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                val zipMd5 = calculateMd5(zipFile)
                // 删除临时文件
                zipFile.delete()
                picList.forEach { it.delete() }
                downloadComicDao.updateWhenComplete(
                    UpdateWhenComplete(
                        comicId,
                        uri,
                        zipMd5
                    )
                )
                toastManager.showAsync("下载成功")
                return Result.success()
            }
            // uri 不存在
            Result.failure()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry() // 如果失败了，系统会自动尝试重试
            } else {
                Result.failure()
            }
        } finally {
            downloadConcurrencyController.release()
        }
    }

    private suspend fun downloadCover(comicId: Int): File {
        return withContext(Dispatchers.IO) {
            val coverUrl =
                "${remoteSettingManager.remoteSettingState.value.imgHost}/media/albums/${comicId}_3x4.jpg"
            val request = ImageRequest.Builder(appContext)
                .data(coverUrl)
                .allowHardware(false)
                .build()

            when (val result = loader.execute(request)) {
                is ErrorResult -> {
                    throw Error("下载封面失败")
                }

                is SuccessResult -> {
                    val bitmap = result.drawable.toBitmap()
                    val file = File(coverDir, "$comicId.webp")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
                    }
                    file
                }
            }
        }
    }

    private suspend fun downloadPicList(comicId: Int, shunt: String): List<File> {
        return withContext(Dispatchers.IO) {
            when (val data = comicRepository.getComicPicList(comicId, shunt)) {
                is NetWorkResult.Error -> {
                    throw Error("下载本子图片列表失败")
                }

                is NetWorkResult.Success<ComicPicListResponse> -> {
                    val dir = getDownloadCacheDir(appContext)
                    data.data.list.mapIndexed { index, url ->
                        val request = ImageRequest.Builder(appContext)
                            .data(url)
                            .allowHardware(false)
                            .build()

                        when (val result = loader.execute(request)) {
                            is ErrorResult -> {
                                throw Error("下载 $index 图片失败")
                            }

                            is SuccessResult -> {
                                val bitmap = result.drawable.toBitmap()
                                val file = File(dir, "$comicId-$index.webp")
                                FileOutputStream(file).use { out ->
                                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
                                }
                                downloadComicDao.updateProgress(
                                    UpdateComicProgress(
                                        comicId,
                                        (index + 1).toFloat() / data.data.list.size
                                    )
                                )
                                file
                            }
                        }
                    }
                }
            }
        }
    }

    private fun zipPicPathList(downloadComic: DownloadComic, picFileList: List<File>): File {
        val filename = "[${downloadComic.id}] ${downloadComic.name}"
        val zipFile = File(getDownloadCacheDir(appContext), "$filename.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            picFileList.forEach { file ->
                if (file.exists()) {
                    val entryName = "$filename/${file.name}"
                    val zipEntry = ZipEntry(entryName)
                    zipOut.putNextEntry(zipEntry)
                    FileInputStream(file).use { fis ->
                        fis.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }
            }
        }
        return zipFile
    }

    private fun calculateMd5(file: File): String {
        val fileSize = file.length()
        val threshold = 1024 * 1024 // 1MB
        val chunkSize = 300 * 1024 // 300KB

        return try {
            MessageDigest.getInstance("MD5").run {
                if (fileSize <= threshold) {
                    // 全量计算
                    file.inputStream().use { inputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            update(buffer, 0, bytesRead)
                        }
                    }
                } else {
                    // 采样计算：开头 + 中间 + 结尾
                    RandomAccessFile(file, "r").use { raf ->
                        val buffer = ByteArray(chunkSize)
                        // 读取开头 300KB
                        raf.readFully(buffer)
                        update(buffer)
                        // 读取中间 300KB
                        val midStart = (fileSize - chunkSize) / 2
                        raf.seek(midStart)
                        raf.readFully(buffer)
                        update(buffer)
                        // 读取结尾 300KB
                        val endStart = fileSize - chunkSize
                        raf.seek(endStart)
                        raf.readFully(buffer)
                        update(buffer)
                    }
                }
                digest().joinToString("") { "%02x".format(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}