package com.par9uet.jm.store

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.database.dao.DownloadComicDao
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.dir.getDownloadCoverDataDir
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.worker.DownloadComicWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadComicDao: DownloadComicDao,
    private val toastManager: ToastManager,
    private val remoteSettingManager: RemoteSettingManager,
    private val localSettingManager: LocalSettingManager
) {
    private val loader = ImageLoader(context)
    suspend fun downloadComic(comic: Comic) {
        downloadCover(comic)
        downloadComicDao.insert(
            DownloadComic(
                id = comic.id,
                name = comic.name,
                authorList = comic.authorList,
                readCount = comic.readCount,
                likeCount = comic.likeCount,
                tagList = comic.tagList,
                roleList = comic.roleList,
                workList = comic.workList,
                status = "pending",
                createTime = System.currentTimeMillis()
            )
        )
        toastManager.show("创建下载任务成功")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // 必须有网
            .build()
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadComicWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    "comicId" to comic.id
                )
            )
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES) // 重试策略
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context).enqueue(downloadRequest)
    }

    private suspend fun downloadCover(comic: Comic) {
        val coverDir = getDownloadCoverDataDir(context)
        val coverUrl =
            "${remoteSettingManager.remoteSettingState.value.imgHost}/media/albums/${comic.id}_3x4.jpg"
        val request = ImageRequest.Builder(context)
            .data(coverUrl)
            .memoryCacheKey("cover-${comic.id}")
            .diskCacheKey("cover-${comic.id}")
            .allowHardware(false)
            .build()

        when (val result = loader.execute(request)) {
            is ErrorResult -> {
                throw Error("下载封面失败")
            }

            is SuccessResult -> {
                val bitmap = result.image.toBitmap()
                val file = File(coverDir, "${comic.id}.webp")
                withContext(Dispatchers.IO) {
                    FileOutputStream(file).use { out ->
                        // TODO 是否分开 cover 和 pic ？
                        compressComicPic(
                            bitmap,
                            localSettingManager.localSettingState.value.comicPicDecodeCompressLevel,
                            out
                        )
                    }
                }
            }
        }
    }
}