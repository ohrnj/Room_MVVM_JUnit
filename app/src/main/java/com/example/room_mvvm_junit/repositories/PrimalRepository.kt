package com.example.room_mvvm_junit.repositories

import com.example.room_mvvm_junit.db.ImageDataModel
import kotlinx.coroutines.flow.Flow

interface PrimalRepository {

    var allImagesFromDao: Flow<List<ImageDataModel>>

    suspend fun insert(imageDataModel: ImageDataModel): Long

    suspend fun update(imageDataModel: ImageDataModel): Int

    suspend fun delete(imageDataModel: ImageDataModel): Int

    suspend fun deleteAll(): Int
}