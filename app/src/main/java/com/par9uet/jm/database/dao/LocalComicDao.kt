package com.par9uet.jm.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.ret.LocalComicWithPic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadArg
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadingStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicErrorStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicPauseStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalComicDao {

    @Query("SELECT * FROM local_comic WHERE status = 'PENDING' or status = 'DOWNLOADING' ORDER BY createTime DESC")
    fun getUnCompleteList(): PagingSource<Int, LocalComic>

    @Query("SELECT * FROM local_comic WHERE status = 'COMPLETE' ORDER BY createTime DESC")
    fun getCompleteList(): PagingSource<Int, LocalComic>

    @Query("SELECT * FROM local_comic WHERE status = :status ORDER BY createTime DESC")
    fun getList(status: DownloadStatus): PagingSource<Int, LocalComic>

    @Query("SELECT * FROM local_comic WHERE comicId = :comicId")
    suspend fun getOne(comicId: Int): LocalComic?

    @Transaction
    @Query("SELECT * FROM local_comic WHERE comicId = :comicId")
    suspend fun getWithLocalComicPic(comicId: Int): LocalComicWithPic

    @Query("SELECT * FROM local_comic WHERE comicId = :id")
    fun getOneFlow(id: Int): Flow<LocalComic?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localComic: LocalComic)

    @Update(entity = LocalComic::class)
    suspend fun updateStatus(updateLocalComicStatus: UpdateLocalComicStatus)

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

    @Query("SELECT * FROM local_comic WHERE comicId in (:idList)")
    fun getListByIdList(idList: List<Int>): Flow<List<LocalComic>>

    @Query("SELECT * FROM local_comic WHERE status = 'DOWNLOADING'")
    suspend fun getDownloadingList(): List<LocalComic>
}