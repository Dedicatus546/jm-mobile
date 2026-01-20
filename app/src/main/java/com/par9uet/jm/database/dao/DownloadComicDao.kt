package com.par9uet.jm.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.par9uet.jm.data.models.DownloadComic
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadComicDao {
    @Query("SELECT * FROM download_comics")
    fun getDownloadingList(): PagingSource<Int, DownloadComic>

    @Query("SELECT * FROM download_comics WHERE status = 'complete'")
    fun getCompleteList(): PagingSource<Int, DownloadComic>

    @Query("SELECT EXISTS(SELECT 1 FROM download_comics WHERE id = :id)")
    fun isExist(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadComic)

    @Update
    suspend fun update(task: DownloadComic)

    @Delete
    suspend fun delete(task: DownloadComic)
}