package com.par9uet.jm.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.del.DeleteLocalComic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadArg
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadingStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicErrorStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicPauseStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalComicDao {

    @Query("SELECT * FROM local_comic WHERE status = :status ORDER BY createTime DESC")
    fun getList(status: DownloadStatus): PagingSource<Int, LocalComic>

    @Query("SELECT * FROM local_comic WHERE status = :status ORDER BY createTime DESC")
    fun getListByStatus(status: DownloadStatus): List<LocalComic>

    @Query("SELECT * FROM local_comic WHERE comicId = :comicId")
    suspend fun get(comicId: Int): LocalComic

    @Query("SELECT * FROM local_comic WHERE comicId = :comicId")
    suspend fun getNullable(comicId: Int): LocalComic?

    @Query("SELECT * FROM local_comic WHERE comicId = :comicId")
    fun getFlow(comicId: Int): Flow<LocalComic>

    @Query("SELECT * FROM local_comic WHERE comicId = :comicId")
    fun getNullableFlow(comicId: Int): Flow<LocalComic?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localComic: LocalComic)

    @Update(entity = LocalComic::class)
    suspend fun updateDownloadingStatus(updateLocalComicDownloadingStatus: UpdateLocalComicDownloadingStatus)

    @Update(entity = LocalComic::class)
    suspend fun updateErrorStatus(updateLocalComicErrorStatus: UpdateLocalComicErrorStatus)

    @Update(entity = LocalComic::class)
    suspend fun updateDownloadArg(updateLocalComicDownloadArg: UpdateLocalComicDownloadArg)

    @Update(entity = LocalComic::class)
    suspend fun updateProgress(updateLocalComicProgress: UpdateLocalComicProgress)

    @Update(entity = LocalComic::class)
    suspend fun updateWhenComplete(updateLocalComicWhenComplete: UpdateLocalComicWhenComplete)

    @Update(entity = LocalComic::class)
    suspend fun updatePauseStatus(updateLocalComicPauseStatusList: List<UpdateLocalComicPauseStatus>)

    @Delete(entity = LocalComic::class)
    suspend fun delete(deleteLocalComic: DeleteLocalComic)

    @Query("SELECT * FROM local_comic WHERE belongComicId = :belongComicId LIMIT 1")
    suspend fun getByBelongComicId(belongComicId: Int): LocalComic?
}