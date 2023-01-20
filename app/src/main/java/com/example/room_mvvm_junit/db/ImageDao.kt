package com.example.room_mvvm_junit.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ImageDao {

    @Query("SELECT * FROM image_data_table")
    fun getAllData(): Flow<List<ImageDataModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(imageDataModel: ImageDataModel): Long

    @Update
    suspend fun updateData(imageDataModel: ImageDataModel): Int

    @Delete
    suspend fun deleteData(imageDataModel: ImageDataModel): Int

    @Query("DELETE FROM image_data_table")
    suspend fun deleteAll(): Int

}








