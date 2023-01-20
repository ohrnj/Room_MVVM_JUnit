package com.example.room_mvvm_junit.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_data_table")
data class ImageDataModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "imageURL")
    var imageURL: String,

    @ColumnInfo(name = "title")
    var title: String
)