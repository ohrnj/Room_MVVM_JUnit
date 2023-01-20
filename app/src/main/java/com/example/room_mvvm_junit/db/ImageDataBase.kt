package com.example.room_mvvm_junit.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImageDataModel::class], version = 1)
abstract class ImageDataBase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        var INSTANCE: ImageDataBase? = null

        fun getInstance(context: Context): ImageDataBase {
            synchronized(ImageDataBase) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder( context.applicationContext,
                        ImageDataBase::class.java,"DB_images"
                    ).build()
                }
                return instance
            }
        }
    }
}