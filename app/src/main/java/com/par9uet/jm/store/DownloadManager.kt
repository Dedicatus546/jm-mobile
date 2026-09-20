package com.par9uet.jm.store

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.par9uet.jm.data.factory.DownloaderFactory
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.data.models.Downloader
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadArg
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.utils.extractPageFromUrl
import com.par9uet.jm.utils.getDownloadComicCoverDir
import com.par9uet.jm.utils.getDownloadComicPicDir
import com.par9uet.jm.utils.log
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localComicDao: LocalComicDao,
    private val localComicPicDao: LocalComicPicDao,
    private val toastManager: ToastManager,
    private val remoteSettingManager: RemoteSettingManager,
    private val localSettingManager: LocalSettingManager,
    private val comicRepository: ComicRepository,
    private val imageLoader: ImageLoader,
    private val coroutineScope: CoroutineScope,
    private val downloaderFactory: DownloaderFactory
) {
    val downloadStatusMap = mutableStateMapOf<Int, CommonUIState<Unit>>()
    private val downloadChannel = Channel<Downloader>(Channel.UNLIMITED)

    init {
        repeat(4) {
            coroutineScope.launch(Dispatchers.IO) {
                for (downloader in downloadChannel) {
                    downloader.download()
                }
            }
        }
    }

    fun downloadComic(comic: Comic, comicChapter: ComicChapter) {
        coroutineScope.launch(Dispatchers.IO) {
            val comicId = comicChapter.id
            val uiState = downloadStatusMap.getOrPut(comicId) {
                CommonUIState(
                    isLoading = true,
                )
            }
            try {
                val localComic = localComicDao.getOne(comicChapter.id)
                if (localComic != null) {
                    when (localComic.status) {
                        // TODO 这里可能要做一些其他处理
                        DownloadStatus.PENDING -> {
                            toastManager.show("该任务已存在，处于等待中")
                        }

                        DownloadStatus.PAUSE -> {
                            toastManager.show("该任务已暂停")
                        }

                        DownloadStatus.DOWNLOADING -> {
                            toastManager.show("该任务已在下载中")
                        }

                        DownloadStatus.ERROR -> {
                            toastManager.show("该任务下载出错")
                        }

                        DownloadStatus.COMPLETE -> {
                            toastManager.show("该任务已下载")
                        }
                    }

                    return@launch
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
                        createTime = createTime
                    )
                )
                insertInfo(comicChapter.id)
                toastManager.show("创建下载任务成功")
                val downloader = downloaderFactory.create(comic, comicChapter)
                downloadChannel.send(downloader)
            } catch (e: Exception) {
                // TODO
            } finally {
                downloadStatusMap[comicId] = uiState.copy(
                    isLoading = false
                )
            }
        }
    }

    private suspend fun downloadCover(comicId: Int) {
        val coverDir = getDownloadComicCoverDir(context)
        val file = File(coverDir, "$comicId.webp")
        if (file.exists()) {
            log("封面存在，跳过下载")
            return
        }
        val coverUrl =
            "${remoteSettingManager.remoteSettingState.value.imgHost}/media/albums/${comicId}_3x4.jpg"
        val request = ImageRequest.Builder(context)
            .data(coverUrl)
            // 下载的时候禁用缓存，强制走请求（不过 okhttp 是否会影响？）
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
        when (val result = imageLoader.execute(request)) {
            is ErrorResult -> {
                throw Error("下载封面失败", result.throwable)
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

    private suspend fun insertInfo(comicId: Int) {
        val shunt = localSettingManager.localSettingState.value.shunt
        val dir = getDownloadComicPicDir(context, comicId)
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
                        path = File(dir, "$page.webp").toUri()
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

