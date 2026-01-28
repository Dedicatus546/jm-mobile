package com.par9uet.jm.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.database.model.UpdateComicCover
import com.par9uet.jm.database.model.UpdateComicProgress
import com.par9uet.jm.database.model.UpdateComicStatus
import com.par9uet.jm.database.model.UpdateComicZipPath
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadComicDao {
    @Query("SELECT * FROM download_comics WHERE status = 'pending' OR status = 'downloading' ORDER BY createTime DESC")
    fun getDownloadingList(): PagingSource<Int, DownloadComic>

    @Query("SELECT * FROM download_comics WHERE status = 'complete' ORDER BY createTime DESC")
    fun getCompleteList(): PagingSource<Int, DownloadComic>

    @Query("SELECT EXISTS(SELECT 1 FROM download_comics WHERE id = :comicId)")
    fun isExist(comicId: Int): Flow<Boolean>

    @Update(entity = DownloadComic::class)
    suspend fun updateCover(updateComicCover: UpdateComicCover)

    @Update(entity = DownloadComic::class)
    suspend fun updateStatus(updateComicStatus: UpdateComicStatus)

    @Update(entity = DownloadComic::class)
    suspend fun updateProgress(updateComicProgress: UpdateComicProgress)

    @Update(entity = DownloadComic::class)
    suspend fun updateZipPath(updateComicZipPath: UpdateComicZipPath)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadComic)

    @Update
    suspend fun update(task: DownloadComic)

    @Delete
    suspend fun delete(task: DownloadComic)
}