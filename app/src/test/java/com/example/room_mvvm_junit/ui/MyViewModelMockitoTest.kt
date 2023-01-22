package com.example.room_mvvm_junit.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.room_mvvm_junit.MainCoroutineRule
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.repositories.DefaultRepository
import com.example.room_mvvm_junit.repositories.FakeTestRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class MyViewModelMockitoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var viewModel: MyViewModel

    private lateinit var img1: ImageDataModel
    private lateinit var img2: ImageDataModel

    @Before
    fun Setup() {
        viewModel = mock(MyViewModel::class.java)

        img1 = ImageDataModel(id=1, "http://www.example.com/img1.png", "image1")
        img2 = ImageDataModel(id=2, "http://www.example.com/img2.png", "image2")
    }

    @Test
    fun `get all data mockitoTest`() {

        val dataResponse = MutableLiveData<List<ImageDataModel>>()
        dataResponse.value = listOf(img1, img2)

        `when`(viewModel.getSavedImages()).then { dataResponse }

        val resultList = viewModel.getSavedImages().value
            assertEquals(2, resultList!!.size)
            assertEquals("image1", resultList[0].title)
            println("title: ${resultList[0].title}")
    }

    @Test
    fun `empty value in imageURL field 2, return false`() {
        val result = viewModel.validateInput(
            "",
            "some image URL"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `empty value in title field, return false`() {
        val result = viewModel.validateInput(
            "http://www.example.com/image2.jpg",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `valid input, return true`() {
        val result = viewModel.validateInput(
            "http://www.example.com/image2.jpg",
            "Sample image"
        )
        assertThat(result).isTrue()
    }


    @Test
    fun `insert image with empty URL in db, return error`() {
        viewModel.insert(ImageDataModel(id = 1, "", "Some image title"))
        val result = viewModel.stmessage.value
        assertThat(result).isEqualTo("The fields must not be empty")
    }

    @Test
    fun `insert data with valid input, return true`() {
        viewModel.insert(ImageDataModel(id = 1, "http://www.example.com/image.jpg", "Sample image"))
        val result = viewModel.stmessage.value
        assertThat(result).isEqualTo("Image successfully inserted")
    }

    @Test
    fun `delete item, return true`() {
        val item = ImageDataModel(id = 1, "www.example.com/image2.jpg", "Sample image")
        viewModel.insert(item)
        viewModel.deleteItem(item)
        assertThat(viewModel.stmessage.value).isEqualTo("1 Row deleted successfully")
    }

    @Test
    fun `delete all items, return true`() {
        val item = ImageDataModel(id = 1, "www.example.com/image2.jpg", "Sample image")
        viewModel.insert(item)
        viewModel.deleteAll()
        assertThat(viewModel.stmessage.value).isNotEqualTo("Error occured")
    }


}




//private fun <T> OngoingStubbing<T>.thenReturn(emptyList: List<T>): OngoingStubbing<T> {
//return thenReturn(emptyList())
//}
