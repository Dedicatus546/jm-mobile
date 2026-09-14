package com.par9uet.jm.store

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import coil3.toBitmap
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.DownloadStatus
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.database.model.update.ResetComicPic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadArg
import com.par9uet.jm.dir.getDownloadCacheDir
import com.par9uet.jm.dir.getDownloadCoverDataDir
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.utils.createComicCoverImageRequest
import com.par9uet.jm.utils.extractPageFromUrl
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.md5
import com.par9uet.jm.worker.DownloadComicWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

val downloadDispatcher = Dispatchers.IO.limitedParallelism(3)

@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localComicDao: LocalComicDao,
    private val localComicPicDao: LocalComicPicDao,
    private val toastManager: ToastManager,
    private val remoteSettingManager: RemoteSettingManager,
    private val localSettingManager: LocalSettingManager,
    private val comicRepository: ComicRepository,
    private val imageLoader: ImageLoader
) {

    // 下载单章节的本子
    suspend fun downloadComic(comic: Comic, comicChapter: ComicChapter) {
        withContext(Dispatchers.IO) {
            val localComic = localComicDao.getOne(comicChapter.id)
            if (localComic != null) {
                toastManager.show("该任务已下载")
                return@withContext
            }
            downloadCover(comic.id)
            val createTime = System.currentTimeMillis()
            localComicDao.insert(
                LocalComic(
                    comicId = comicChapter.id,
                    belongComicId = comic.id,
                    name = comic.name,
                    chapterName = comicChapter.name,
                    authorList = comic.authorList,
                    readCount = comic.readCount,
                    likeCount = comic.likeCount,
                    tagList = comic.tagList,
                    roleList = comic.roleList,
                    workList = comic.workList,
                    status = DownloadStatus.PENDING,
                    createTime = createTime,
                    comicKey = ""
                )
            )
            insertInfo(comicChapter.id, localSettingManager.localSettingState.value.shunt)
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
                .build()
            WorkManager.getInstance(context).enqueue(downloadRequest)
        }
    }

    suspend fun recoveryDownloadComic(comic: Comic, comicChapter: ComicChapter) {
        withContext(Dispatchers.IO) {
            val localComicWithLocalComicPic = localComicDao.getWithLocalComicPic(comicChapter.id)
            if (localComicWithLocalComicPic == null) {
                toastManager.show("该任务不存在")
                return@withContext
            }
            val localComic = localComicWithLocalComicPic.localComic
            val localComicPicList = localComicWithLocalComicPic.localComicPicList
            downloadCover(comic.id)
            if (localComicPicList.isEmpty()) {
                insertInfo(comicChapter.id, localSettingManager.localSettingState.value.shunt)
            } else {
                val dir = getDownloadCacheDir(context)
                // 将那些不匹配的已下载文件重置
                localComicPicDao.reset(
                    localComicPicList.filter { it.isComplete }.filter {
                        val file = File(dir, "${it.comicId}-${it.page}.webp")
                        if (file.exists()) {
                            val md5 = md5(file)
                            if (md5 != it.md5) {
                                file.delete()
                                true
                            } else {
                                false
                            }
                        } else {
                            true
                        }
                    }.map {
                        ResetComicPic(
                            comicId = it.comicId,
                            url = it.url
                        )
                    }
                )
            }
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
                .build()
            WorkManager.getInstance(context).enqueue(downloadRequest)
        }
    }

    private suspend fun downloadCover(comicId: Int) {
        val coverDir = getDownloadCoverDataDir(context)
        val file = File(coverDir, "$comicId.webp")
        if (file.exists()) {
            log("封面存在，跳过下载")
            return
        }
        val coverUrl =
            "${remoteSettingManager.remoteSettingState.value.imgHost}/media/albums/${comicId}_3x4.jpg"
        val request = createComicCoverImageRequest(
            context = context,
            comicId = comicId,
            url = coverUrl
        )
        when (val result = imageLoader.execute(request)) {
            is ErrorResult -> {
                throw Error("下载封面失败")
            }

            is SuccessResult -> {
                val bitmap = result.image.toBitmap()

                withContext(Dispatchers.IO) {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                Bitmap.CompressFormat.WEBP_LOSSLESS
                            } else {
                                Bitmap.CompressFormat.WEBP
                            },
                            100,
                            out
                        )
                    }
                }
            }
        }
    }

    private suspend fun insertInfo(comicId: Int, shunt: String) {
        val dir = getDownloadCacheDir(context)
        when (val data = comicRepository.getComicPicList(comicId, shunt)) {
            is NetworkResult.Error -> {
                throw Error("下载本子图片列表失败")
            }

            is NetworkResult.Success<ComicPicListResponse> -> {
                val scrambleId = data.data.__scrambleId
                val speed = data.data.__speed
                localComicPicDao.insert(data.data.list.map {
                    val page = extractPageFromUrl(it)
                    LocalComicPic(
                        comicId = comicId,
                        url = it,
                        page = page,
                        path = File(dir, "$comicId-$page.webp").toUri()
                    )
                })
                localComicDao.updateDownloadArg(
                    UpdateLocalComicDownloadArg(
                        comicId,
                        scrambleId,
                        speed
                    )
                )
            }
        }
    }
}