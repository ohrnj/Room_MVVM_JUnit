package com.example.room_mvvm_junit.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.room_mvvm_junit.MainCoroutineRule
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.repositories.FakeTestRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(MockitoJUnitRunner::class)
class MyViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeRepository: FakeTestRepository
    private lateinit var viewModel: MyViewModel
    private lateinit var img1: ImageDataModel
    private lateinit var img2: ImageDataModel

    @Before
    fun Setup() {
        fakeRepository = FakeTestRepository()
        viewModel = MyViewModel(fakeRepository)

        img1 = ImageDataModel(id = 1, "http://www.example.com/img1.png", "image1")
        img2 = ImageDataModel(id = 2, "http://www.example.com/img2.png", "image2")
    }

    @Test
    fun `insert data, get list from livedata, return success if equal`() {

        viewModel.insert(ImageDataModel(id = 1, "http://www.example.com/img1.png", "image1"))

        assertEquals(viewModel.getSavedImages().getOrAwaitValue()[0].title, "image1")
        assertEquals(viewModel.getSavedImages().getOrAwaitValue()[0], img1)
    }


    @Test
    fun `insert data, check if data are inserted and return success`(){

        viewModel.insert(img1)

        val latch = CountDownLatch(1)
        val observer = Observer<List<ImageDataModel>> {
                assertEquals(img1, it[0])
                latch.countDown()
            }
        viewModel.getSavedImages().observeForever(observer)

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw TimeoutException("LiveData value was never set.")
        }
    }


    @Test
    fun `verify user input, empty value in imageURL input field, return false`() {
        val result = viewModel.validateInput(
            "image1",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `verify user input, empty value in title input field, return false`() {
        val result = viewModel.validateInput(
            "",
            "http://www.example.com/image2.jpg"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `verify input form, valid input, return true`() {
        val result = viewModel.validateInput(
            "http://www.example.com/image2.jpg",
            "image2"
        )
        assertThat(result).isTrue()
    }


    @Test
    fun `insert image with empty URL in db, return error`() {
        viewModel.insert(ImageDataModel(id = 1, "", "image1"))
        val result = viewModel.stmessage.value
        assertThat(result).isEqualTo("The fields must not be empty")
    }

    @Test
    fun `insert data with valid input, return true`() {
        viewModel.insert(ImageDataModel(id = 1, "http://www.example.com/image1.jpg", "image1"))
        val result = viewModel.stmessage.value
        assertThat(result).isEqualTo("Image successfully inserted")
    }

    @Test
    fun `delete item, return true`() {
        val item = ImageDataModel(id = 1, "www.example.com/image2.jpg", "image2")
        viewModel.insert(item)
        viewModel.deleteItem(item)
        assertThat(viewModel.stmessage.value).isEqualTo("1 Row deleted successfully")
    }

    @Test
    fun `delete all items, return true`() {
        val item = ImageDataModel(id = 1, "www.example.com/image2.jpg", "image2")
        viewModel.insert(item)
        viewModel.deleteAll()
        assertThat(viewModel.stmessage.value).isNotEqualTo("Error occured")
    }


    //This function observes a LiveData until it receives a new value (via onChanged) and then
    // it removes the observer. If the LiveData already has a value, it returns it immediately.
    // Additionally, if the value is never set, it will throw an exception after 2 seconds.
    // This prevents tests that never finish when something goes wrong.

    /* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }


}

