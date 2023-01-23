package com.example.room_mvvm_junit.repositories

import com.example.room_mvvm_junit.db.ImageDao
import com.example.room_mvvm_junit.db.ImageDataModel
import javax.inject.Inject


class DefaultRepository @Inject constructor(private val myDao: ImageDao) : BaseRepository {

    override var allImagesFromDao = myDao.getAllData()

    override suspend fun insert(imageDataModel: ImageDataModel): Long {
        return myDao.insertData(imageDataModel)    }

    override suspend fun update(imageDataModel: ImageDataModel): Int {
        return myDao.updateData(imageDataModel)    }

    override suspend fun delete(imageDataModel: ImageDataModel): Int {
        return myDao.deleteData(imageDataModel)    }

    override suspend fun deleteAll(): Int {
        return myDao.deleteAll()    }
}