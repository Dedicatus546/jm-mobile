package com.par9uet.jm.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.par9uet.jm.database.model.DeleteComic
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.database.model.UpdateComicProgress
import com.par9uet.jm.database.model.UpdateComicStatus
import com.par9uet.jm.database.model.UpdateWhenComplete
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadComicDao {

    @Query("SELECT * FROM download_comics WHERE status = 'pending' or status = 'downloading' ORDER BY createTime DESC")
    fun getUnCompleteList(): PagingSource<Int, DownloadComic>

    @Query("SELECT * FROM download_comics WHERE status = 'complete' ORDER BY createTime DESC")
    fun getCompleteList(): PagingSource<Int, DownloadComic>

    @Query("SELECT * FROM download_comics WHERE id = :id")
    fun getOne(id: Int): DownloadComic

    @Query("SELECT * FROM download_comics WHERE id = :id")
    fun getOneFlow(id: Int): Flow<DownloadComic?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadComic)

    @Update(entity = DownloadComic::class)
    suspend fun updateStatus(updateComicStatus: UpdateComicStatus)

    @Update(entity = DownloadComic::class)
    suspend fun updateProgress(updateComicProgress: UpdateComicProgress)

    @Update(entity = DownloadComic::class)
    suspend fun updateWhenComplete(updateWhenComplete: UpdateWhenComplete)

    @Delete(entity = DownloadComic::class)
    suspend fun delete(deleteComic: DeleteComic)
}