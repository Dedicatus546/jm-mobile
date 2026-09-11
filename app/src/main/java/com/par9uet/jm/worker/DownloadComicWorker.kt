package com.par9uet.jm.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import coil3.toBitmap
import com.par9uet.jm.controller.DownloadConcurrencyController
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import com.par9uet.jm.dir.getDownloadCacheDir
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.createComicOriginalPicImageRequest
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.extractPageFromUrl
import com.par9uet.jm.utils.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@HiltWorker
class DownloadComicWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val localComicDao: LocalComicDao,
    private val localSettingManager: LocalSettingManager,
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager,
    private val downloadConcurrencyController: DownloadConcurrencyController,
    private val imageLoader: ImageLoader,
) : CoroutineWorker(appContext, params) {

    private val notificationId = id.hashCode()
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val GROUP_KEY_DOWNLOADS = "com.par9uet.jm.download_group"
    }

    override suspend fun doWork(): Result {
        val comicId = inputData.getInt("comicId", -1)
        if (comicId == -1) {
            log("comicId 不存在")
            return Result.failure()
        }
        downloadConcurrencyController.acquire()
        createNotificationChannel()
        setForeground(getForegroundInfo(0))
        return try {
            val localComic = localComicDao.getOne(comicId)
            if (localComic == null) {
                log("comicId $comicId 任务不存在")
                return Result.failure()
            }
            localComicDao.updateStatus(
                UpdateLocalComicStatus(
                    comicId,
                    "downloading"
                )
            )
            val picList =
                downloadPicList(comicId, localSettingManager.localSettingState.value.shunt)
            val zipFile = zipPicPathList(localComic.comicId, localComic.name, picList)
            var uri = createUri(zipFile.name)
            uri?.let {
                // 删除原有文件，防止出现（1）文件名后缀
                applicationContext.contentResolver.query(uri, null, null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            applicationContext.contentResolver.delete(uri, null, null)
                        }
                    }
            }
            // 重新获取 uri ，不可对同一个 uri 执行多个操作，否则报错
            uri = createUri(zipFile.name)
            uri?.let {
                applicationContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    zipFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                val zipMd5 = calculateMd5(zipFile)
                // 删除临时文件
                zipFile.delete()
                picList.forEach { it.delete() }
                localComicDao.updateWhenComplete(
                    UpdateLocalComicWhenComplete(
                        comicId,
                        uri,
                        zipMd5
                    )
                )
                toastManager.show("下载成功")
                return Result.success()
            }
            // uri 不存在
            Result.failure()
        } catch (e: Exception) {
            log("下载过程出错，${e.stackTraceToString()}")
            Result.failure()
        } finally {
            downloadConcurrencyController.release()
        }
    }

    private suspend fun downloadPicList(comicId: Int, shunt: String): List<File> {
        return withContext(Dispatchers.IO) {
            when (val data = comicRepository.getComicPicList(comicId, shunt)) {
                is NetworkResult.Error -> {
                    throw Error("下载本子图片列表失败")
                }

                is NetworkResult.Success<ComicPicListResponse> -> {
                    val dir = getDownloadCacheDir(applicationContext)
                    val scrambleId = data.data.__scrambleId
                    val speed = data.data.__speed
                    data.data.list.mapIndexed { index, url ->
                        val request = createComicOriginalPicImageRequest(
                            context = applicationContext,
                            url = url,
                            comicId = comicId
                        )

                        when (val result = imageLoader.execute(request)) {
                            is ErrorResult -> {
                                throw Error("下载 $index 图片失败")
                            }

                            is SuccessResult -> {
                                // TODO 这里或许可以利用缓存？
                                val originalBitmap = result.image.toBitmap()
                                val page = extractPageFromUrl(url)
                                val decodedBitmap = decodeComicPicBitmap(
                                    url,
                                    originalBitmap,
                                    comicId,
                                    scrambleId,
                                    speed,
                                    page
                                )
                                val file = File(dir, "$comicId-$index.webp")
                                FileOutputStream(file).use { out ->
                                    compressComicPic(
                                        decodedBitmap,
                                        localSettingManager.localSettingState.value.comicPicDecodeCompressLevel,
                                        out
                                    )
                                }
                                val progress = (index + 1).toFloat() / data.data.list.size
                                localComicDao.updateProgress(
                                    UpdateLocalComicProgress(
                                        comicId,
                                        progress
                                    )
                                )
                                setForeground(getForegroundInfo((progress * 100).toInt()))
                                file
                            }
                        }
                    }
                }
            }
        }
    }

    private fun zipPicPathList(
        comicId: Int,
        name: String,
        picFileList: List<File>
    ): File {
        val filename =
            "[${comicId}]${name}".filter { it.isLetterOrDigit() || it == '[' || it == ']' || it == ' ' }
        val zipFile = File(getDownloadCacheDir(applicationContext), "$filename.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            picFileList.forEach { file ->
                if (file.exists()) {
                    val entryName = "${file.name}"
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

    private fun createUri(filename: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            // 保存到 Download/jm-mobile 目录下
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/jm-mobile"
            )
        }

        val uri =
            applicationContext.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

        return uri
    }

    // WorkManager 要求重写该方法以支持前台服务
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(0)
    }

    private fun getForegroundInfo(progress: Int): ForegroundInfo {
        val title = "文件下载中"
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$progress%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true) // 禁止用户滑动清除
            .setProgress(100, progress, false) // 显示系统进度条
            .addAction(android.R.drawable.ic_delete, "取消下载", cancelIntent) // 增加取消按钮
            .build()

        return ForegroundInfo(
            notificationId,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "文件下载通知",
            NotificationManager.IMPORTANCE_LOW // 低优先级避免每次更新进度都发出蜂鸣提示
        )
        notificationManager.createNotificationChannel(channel)
    }
}