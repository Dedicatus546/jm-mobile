package com.par9uet.jm.repository

import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.database.model.update.UpdateLocalComicPicWhenComplete
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class LocalComicPicRepository @Inject constructor(
    private val localComicPicDao: LocalComicPicDao
) : BaseLocalRepository() {
    suspend fun updateLocalComicPicCompleteStatus(
        comicId: Int,
        page: String,
        md5: String,
    ): DbResult<Unit> {
        return safeApiCall {
            localComicPicDao.updateComplete(
                UpdateLocalComicPicWhenComplete(
                    comicId = comicId,
                    page = page,
                    md5 = md5,
                )
            )
        }
    }

    suspend fun getLocalComicPicList(comicId: Int): DbResult<List<LocalComicPic>> {
        return safeApiCall {
            localComicPicDao.getLocalComicPicList(comicId)
        }
    }

    suspend fun addLocalComicPicList(localComicPicList: List<LocalComicPic>): DbResult<Unit> {
        return safeApiCall {
            localComicPicDao.insert(localComicPicList)
        }
    }

    suspend fun delete(comicId: Int): DbResult<Unit> {
        return safeApiCall {
            localComicPicDao.deleteByComicId(comicId)
        }
    }
}