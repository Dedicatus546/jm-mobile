package com.par9uet.jm.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalComicDao {

    @Transaction
    @Query("SELECT * FROM local_comic WHERE status = 'pending' or status = 'downloading' ORDER BY createTime DESC")
    fun getUnCompleteList(): PagingSource<Int, LocalComic>

    @Transaction
    @Query("SELECT * FROM local_comic WHERE status = 'complete' ORDER BY createTime DESC")
    fun getCompleteList(): PagingSource<Int, LocalComic>

    @Query("SELECT * FROM local_comic WHERE comicId = :id")
    fun getOne(id: Int): LocalComic?

    @Query("SELECT * FROM local_comic WHERE comicId = :id")
    fun getOneFlow(id: Int): Flow<LocalComic?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localComic: LocalComic)

    @Update(entity = LocalComic::class)
    suspend fun updateStatus(updateLocalComicStatus: UpdateLocalComicStatus)

    @Update(entity = LocalComic::class)
    suspend fun updateProgress(updateLocalComicProgress: UpdateLocalComicProgress)

    @Update(entity = LocalComic::class)
    suspend fun updateWhenComplete(updateLocalComicWhenComplete: UpdateLocalComicWhenComplete)

    @Transaction
    @Query("SELECT * FROM local_comic WHERE comicId in (:idList)")
    fun getListByIdList(idList: List<Int>): Flow<List<LocalComic>>

}