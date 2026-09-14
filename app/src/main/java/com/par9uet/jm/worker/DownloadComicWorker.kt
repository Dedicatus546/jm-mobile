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
import androidx.core.net.toFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import coil3.toBitmap
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.del.DeleteLocalComicPic
import com.par9uet.jm.database.model.ret.LocalComicWithPic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadingStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicErrorStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicPicWhenComplete
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import com.par9uet.jm.dir.getDownloadCacheDir
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.createComicOriginalPicImageRequest
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.md5
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

val downloadDispatcher = Dispatchers.IO.limitedParallelism(3)

@HiltWorker
class DownloadComicWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val localComicDao: LocalComicDao,
    private val localComicPicDao: LocalComicPicDao,
    private val localSettingManager: LocalSettingManager,
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager,
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
        createNotificationChannel()
        setForeground(getForegroundInfo(0))
        return withContext(downloadDispatcher) {
            try {
                val localComicWithPic = localComicDao.getWithLocalComicPic(comicId)
                if (localComicWithPic == null) {
                    log("comicId $comicId 任务不存在")
                    return@withContext Result.failure()
                }
                localComicDao.updateDownloadingStatus(
                    UpdateLocalComicDownloadingStatus(
                        comicId,
                    )
                )
                val localComicPic = localComicWithPic.localComic
                val localComicPicList = localComicWithPic.localComicPicList
                val picList =
                    downloadPicList(localComicWithPic)
                val zipFile = zipPicPathList(
                    localComicPic.comicId,
                    localComicPic.name + if (localComicPic.chapterName.isNotEmpty()) " [${localComicPic.chapterName}]" else "",
                    picList
                )
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
                    val zipMd5 = md5(zipFile)
                    // 删除临时文件
                    zipFile.delete()
                    picList.forEach { it.delete() }
                    localComicPicDao.delete(
                        localComicPicList.map {
                            DeleteLocalComicPic(
                                it.comicId,
                                it.url
                            )
                        }
                    )
                    localComicDao.updateWhenComplete(
                        UpdateLocalComicWhenComplete(
                            comicId,
                            uri,
                            zipMd5
                        )
                    )
                    toastManager.show("下载成功")
                    return@withContext Result.success()
                }
                // uri 不存在
                Result.failure()
            } catch (e: Exception) {
                log("下载过程出错，${e.stackTraceToString()}")
                localComicDao.updateErrorStatus(
                    UpdateLocalComicErrorStatus(
                        comicId,
                        e.stackTraceToString()
                    )
                )
                Result.failure()
            }
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    private suspend fun downloadPicList(localComicWithPic: LocalComicWithPic): List<File> {
        val scrambleId = localComicWithPic.localComic.scrambleId
        val speed = localComicWithPic.localComic.speed
        val size = localComicWithPic.localComicPicList.size
        val completeCount = AtomicInt(0)
        return localComicWithPic.localComicPicList.mapIndexed { _, item ->
            withContext(Dispatchers.IO) {
                checkStop()
                val file = item.path.toFile()
                if (item.isComplete) {
                    return@withContext file
                }
                val request = createComicOriginalPicImageRequest(
                    context = applicationContext,
                    url = item.url,
                    comicId = item.comicId
                )

                when (val result = imageLoader.execute(request)) {
                    is ErrorResult -> {
                        throw Error("下载 ${item.page} 图片失败")
                    }

                    is SuccessResult -> {
                        // TODO 这里或许可以利用缓存？
                        val originalBitmap = result.image.toBitmap()
                        val decodedBitmap = decodeComicPicBitmap(
                            item.url,
                            originalBitmap,
                            item.comicId,
                            scrambleId!!,
                            speed!!,
                            item.page
                        )
                        val file = item.path.toFile()
                        FileOutputStream(file).use { out ->
                            compressComicPic(
                                decodedBitmap,
                                localSettingManager.localSettingState.value.comicPicDecodeCompressLevel,
                                out
                            )
                        }
                        val progress = completeCount.addAndFetch(1) * 1.0f / size
                        localComicDao.updateProgress(
                            UpdateLocalComicProgress(
                                item.comicId,
                                progress
                            )
                        )
                        localComicPicDao.updateComplete(
                            UpdateLocalComicPicWhenComplete(
                                comicId = item.comicId,
                                url = item.url,
                                md5 = md5(file),
                            )
                        )
                        setForeground(getForegroundInfo((progress * 100).toInt()))
                        file
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

    private fun checkStop() {
        if (isStopped) {
            throw Error("下载已被取消")
        }
    }
}