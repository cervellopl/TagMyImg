package com.cervellopl.tagmyimg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: ImageEntity): Long

    @Insert
    suspend fun insertTags(tags: List<TagEntity>)

    @Transaction
    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getImagesWithTags(): Flow<List<ImageWithTags>>
}
