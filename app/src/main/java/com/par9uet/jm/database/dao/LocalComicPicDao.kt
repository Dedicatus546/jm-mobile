package com.par9uet.jm.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.database.model.update.UpdateLocalComicPicWhenComplete

@Dao
interface LocalComicPicDao {

    @Query("SELECT * FROM local_comic_pic WHERE comicId = :comicId")
    suspend fun getLocalComicPicList(comicId: Int): List<LocalComicPic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<LocalComicPic>)

    @Update(entity = LocalComicPic::class)
    suspend fun updateComplete(updateLocalComicPicWhenComplete: UpdateLocalComicPicWhenComplete)

    @Query("DELETE from local_comic_pic where comicId = :comicId")
    suspend fun deleteByComicId(comicId: Int)
}