package com.example.room_mvvm_junit.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.room_mvvm_junit.MainCoroutineRule
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.ui.MyViewModel
//import io.mockk.Invocation
//import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class DefaultRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mockRepository: DefaultRepository

    private lateinit var img1: ImageDataModel
    private lateinit var img2: ImageDataModel

    @Before
    fun Setup() {
        mockRepository = Mockito.mock<DefaultRepository>()

        img1 = ImageDataModel(id = 1, "http://www.example.com/img1.png", "image1")
        img2 = ImageDataModel(id = 2, "http://www.example.com/img2.png", "image2")
    }

    @Test
    fun `get All Images From Dao, return correct size`() = runBlocking {

        whenever(mockRepository.allImagesFromDao).thenAnswer { flow { emit(listOf(img1, img2)) } }

        mockRepository.allImagesFromDao.collect { item ->
            assertNotNull(item)
            assertEquals(2, item.size)
        }
    }

    @Test
    fun `get All Images From Dao, return the same content`() = runBlocking {

        whenever(mockRepository.allImagesFromDao).thenAnswer { flow { emit(listOf(img1, img2)) } }

        mockRepository.allImagesFromDao.collect { item ->
            assertEquals("image1", item[0].title)
        }
    }


    @Test
    fun `insert data, check interaction and return number of inserted rows`() = runBlocking {
        whenever(mockRepository.insert(img1)).thenReturn(1) // return the number of added rows

        mockRepository.insert(img1)
        org.mockito.kotlin.verify(mockRepository).insert(img1)

        assertEquals(1, mockRepository.insert(img1))
    }

    @Test
    fun `update data, result must be number of updated rows`() = runBlocking {
        whenever(mockRepository.update(img1)).thenReturn(1) // return the number of updated rows

        mockRepository.insert(img1)
        org.mockito.kotlin.verify(mockRepository).insert(img1)

        assertEquals(1, mockRepository.update(img1))
    }

    @Test
    fun `delete data, verify interaction and return number of deleted rows`(): Unit = runBlocking {
        whenever(mockRepository.delete(img1)).thenReturn(1) // return the number of deleted rows

        mockRepository.delete(img1)
        org.mockito.kotlin.verify(mockRepository).delete(img1)
        assertEquals(1, mockRepository.delete(img1))
    }

    @Test
    fun `delete all data, verify call, result is number of deleted rows`() = runBlocking {
        doReturn(1).whenever(mockRepository).deleteAll()

        mockRepository.deleteAll()
        verify(mockRepository, only()).deleteAll()

        assertEquals(1, mockRepository.deleteAll())
    }

}