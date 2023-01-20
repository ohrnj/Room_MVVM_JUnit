package com.example.room_mvvm_junit.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.room_mvvm_junit.R
import com.example.room_mvvm_junit.db.ImageDao
import com.example.room_mvvm_junit.db.ImageDataBase
import com.example.room_mvvm_junit.repositories.DefaultRepository
import com.example.room_mvvm_junit.repositories.PrimalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideImageDao(dataBase: ImageDataBase) = dataBase.imageDao()

    @Provides
    @Singleton
    fun provideImageDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ImageDataBase::class.java, "db_name").build()

    @Provides
    @Singleton
    fun provideDefaultRepository(dao: ImageDao) = DefaultRepository(dao) as PrimalRepository

    @Provides
    @Singleton
    fun provideGlideInstance(@ApplicationContext context: Context) =
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions().placeholder(R.drawable.ic_baseline_add_to_photos_24)
                    .error(R.drawable.ic_baseline_add_to_photos_24)
            )

}