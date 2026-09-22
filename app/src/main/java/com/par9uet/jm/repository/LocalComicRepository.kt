package com.par9uet.jm.repository

import androidx.paging.PagingSource
import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.del.DeleteLocalComic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadArg
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadingStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicErrorStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicPauseStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class LocalComicRepository @Inject constructor(
    private val localComicDao: LocalComicDao
) : BaseLocalRepository() {
    fun getLocalComicFlow(comicId: Int): DbResult<Flow<LocalComic>> {
        return safeSyncApiCall {
            localComicDao.getFlow(comicId)
        }
    }

    fun getNullableLocalComicFlow(comicId: Int): DbResult<Flow<LocalComic?>> {
        return safeSyncApiCall {
            localComicDao.getNullableFlow(comicId)
        }
    }

    fun getLocalComicListPagingSource(status: DownloadStatus): DbResult<PagingSource<Int, LocalComic>> {
        return safeSyncApiCall {
            localComicDao.getList(status)
        }
    }

    suspend fun getLocalComic(comicId: Int): DbResult<LocalComic> {
        return safeApiCall {
            localComicDao.get(comicId)
        }
    }

    suspend fun getNullableLocalComic(comicId: Int): DbResult<LocalComic?> {
        return safeApiCall {
            localComicDao.getNullable(comicId)
        }
    }

    suspend fun updateLocalComicDownloadingStatus(comicId: Int): DbResult<Unit> {
        return safeApiCall {
            localComicDao.updateDownloadingStatus(
                UpdateLocalComicDownloadingStatus(
                    comicId,
                )
            )
        }
    }

    suspend fun updateLocalComicCompleteStatus(comicId: Int): DbResult<Unit> {
        return safeApiCall {
            localComicDao.updateWhenComplete(
                UpdateLocalComicWhenComplete(
                    comicId,
                )
            )
        }
    }

    suspend fun updateLocalComicErrorStatus(comicId: Int, errorMessage: String): DbResult<Unit> {
        return safeApiCall {
            localComicDao.updateErrorStatus(
                UpdateLocalComicErrorStatus(
                    comicId,
                    errorMessage
                )
            )
        }
    }

    suspend fun updateLocalComicProgress(comicId: Int, progress: Float): DbResult<Unit> {
        return safeApiCall {
            localComicDao.updateProgress(
                UpdateLocalComicProgress(
                    comicId,
                    progress
                )
            )
        }
    }

    suspend fun addLocalComic(localComic: LocalComic): DbResult<Unit> {
        return safeApiCall {
            localComicDao.insert(localComic)
        }
    }

    suspend fun updateLocalComicDownloadArg(
        comicId: Int,
        scrambleId: Int,
        speed: String,
    ): DbResult<Unit> {
        return safeApiCall {
            localComicDao.updateDownloadArg(
                UpdateLocalComicDownloadArg(
                    comicId,
                    scrambleId,
                    speed
                )
            )
        }
    }

    suspend fun delete(comicId: Int): DbResult<Unit> {
        return safeApiCall {
            localComicDao.delete(
                DeleteLocalComic(
                    comicId = comicId
                )
            )
        }
    }

    suspend fun updateLocalComicPauseStatus(comicIdList: List<Int>): DbResult<Unit> {
        return safeApiCall {
            localComicDao.updatePauseStatus(comicIdList.map {
                UpdateLocalComicPauseStatus(
                    comicId = it
                )
            })
        }
    }


    suspend fun getLocalComicList(status: DownloadStatus): DbResult<List<LocalComic>> {
        return safeApiCall {
            localComicDao.getListByStatus(status)
        }
    }
}