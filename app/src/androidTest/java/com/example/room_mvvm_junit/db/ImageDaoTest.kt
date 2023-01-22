package com.example.room_mvvm_junit.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ImageDaoTest {

    private lateinit var database: ImageDataBase
    private lateinit var dao: ImageDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ImageDataBase::class.java )
            .allowMainThreadQueries()
            .build()
        dao = database.imageDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insert_Image_In_Db_Return_True() = runTest {
        val imageItem = ImageDataModel(id = 1, "url1", "image1")
        dao.insertData(imageItem)

        // Get data from Flow
        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            dao.getAllData().collect { items ->
                assertTrue(items.contains(imageItem))
                latch.countDown()
            }
        }
        latch.await(2, TimeUnit.SECONDS)
        job.cancel()
    }

    @Test
    fun update_Image_In_Db_return_true_if_contain_new_data() = runBlocking {
        val imageItem = ImageDataModel(id = 1, "url1", "image1")
        dao.insertData(imageItem)
        val updatedImageItem = ImageDataModel(id = 1, "url2", "image2")
        dao.updateData(updatedImageItem)

        val imageList = getDataFromFlow()
        assertTrue(imageList.contains(updatedImageItem))
    }

    @Test
    fun delete_Image_From_Db_should_not_contain_that_image_anymore() = runBlocking {
        val imageItem = ImageDataModel(id = 2, "url1", "image2")
        dao.insertData(imageItem)
        dao.deleteData(imageItem)

        val imageList = getDataFromFlow()
        assertThat(imageList).doesNotContain(imageItem)
    }

    @Test
    fun delete_All_Images_From_Db_Return_Empty() = runBlocking {
        var imageItem = ImageDataModel(id = 1, "url1", "image1")
        dao.insertData(imageItem)
        imageItem = ImageDataModel(id = 2, "url2", "image2")
        dao.insertData(imageItem)
        dao.deleteAll()

        val imageList = getDataFromFlow()
        assertThat(imageList).isEmpty()
    }


    private fun getDataFromFlow() = runBlocking {
        val latch = CountDownLatch(1)
        val imageList = mutableListOf<ImageDataModel>()
        val job = launch(Dispatchers.IO) {
            dao.getAllData().collect { items ->
                imageList.addAll(items)
                latch.countDown()
            }
        }
        withContext(Dispatchers.IO) {
            latch.await(2, TimeUnit.SECONDS)
        }
        job.cancelAndJoin()
        return@runBlocking imageList
    }


}