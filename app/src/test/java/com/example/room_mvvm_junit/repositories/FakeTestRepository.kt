package com.example.room_mvvm_junit.repositories

import com.example.room_mvvm_junit.db.ImageDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeTestRepository: BaseRepository {

    private val imageItemList = mutableListOf<ImageDataModel>()

    override var allImagesFromDao: Flow<List<ImageDataModel>> = flow { emit(imageItemList) }

    override suspend fun insert(imageDataModel: ImageDataModel): Long {
        imageItemList.add(imageDataModel)
        return 1    }

    override suspend fun update(imageDataModel: ImageDataModel): Int {
        imageItemList.add(imageDataModel)
        return 1    }

    override suspend fun delete(imageDataModel: ImageDataModel): Int {
        imageItemList.remove(imageDataModel)
        return 1    }

    override suspend fun deleteAll(): Int {
        imageItemList.removeAll(imageItemList)
        return 1    }
}