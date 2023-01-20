package com.example.room_mvvm_junit.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.bumptech.glide.load.engine.Resource
import com.example.room_mvvm_junit.MainCoroutineRule
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.repositories.DefaultRepository
import com.example.room_mvvm_junit.repositories.FakeTestRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.stubbing.OngoingStubbing

class MyViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Mock
    private lateinit var imageObserver: Observer<List<ImageDataModel>>

    private lateinit var viewModel: MyViewModel

    private var repository = mock<DefaultRepository>()
    private lateinit var viewModelWithMockito: MyViewModel

    @Before
    fun Setup() {
        viewModel = MyViewModel(FakeTestRepository())
        viewModelWithMockito = MyViewModel(repository)
    }

/*    @Test
    fun `unable to fetch items, return false`() = runBlocking {
        whenever(repository.allImagesFromDao).thenReturn(emptyList())
        viewModelWithMockito = MyViewModel(repository)
        Assert.assertEquals(State.SUCCESS(emptyList()), viewModelWithMockito.state.value)
    }*/

//    @Test
//    fun `unable to fetch items, return false`() = runBlocking {
//        whenever(repository.allImagesFromDao).thenThrow(RuntimeException("Error"))
//        viewModelWithMockito = MyViewModel(repository)
//        viewModelWithMockito.getSavedImages().observeForever(imageObserver)
//verify(viewModelWithMockito).getSavedImages()
//        viewModelWithMockito.getSavedImages().removeObserver(imageObserver)
//        Assert.assertEquals(State.FAILURE("Error"), viewModelWithMockito.state.value)
//    }

    @Test
    fun `empty value in imageURL field, return false`() {
        val result = viewModelWithMockito.validateInput(
            "",
            "some image URL"
        )
        assertThat(result).isFalse()
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
