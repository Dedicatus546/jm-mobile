package com.par9uet.jm.repository

import androidx.core.net.toFile
import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.md5
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class LocalComicRepository @Inject constructor(
    private val localComicDao: LocalComicDao,
    private val localComicPicDao: LocalComicPicDao
) {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): DbResult<T> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiCall()
            }
            DbResult.Success(response)
        } catch (e: Throwable) {
            handleException(e)
        }
    }

    private fun handleException(e: Throwable): DbResult.Error {
        log("获取数据失败，${e.stackTraceToString()}")
        return DbResult.Error(
            e.message ?: "读取数据错误"
        )
    }

    suspend fun getComicDetail(comicId: Int): DbResult<LocalComic?> {
        return safeApiCall {
            localComicDao.getOne(comicId)
        }
    }

    suspend fun getComicPicList(comicId: Int): DbResult<List<LocalComicPic>> {
        return safeApiCall {
            val list = localComicPicDao.getLocalComicPicList(comicId)
            if (list.any { !it.isComplete }) {
                throw Error("任务下载未完成")
            }
            if (
                list.any {
                    val md5 = md5(it.path.toFile())
                    md5 != it.md5
                }
            ) {
                throw Error("MD5 校验失败")
            }
            list
        }
    }
}