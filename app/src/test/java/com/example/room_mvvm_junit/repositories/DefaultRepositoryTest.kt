package com.example.room_mvvm_junit.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.room_mvvm_junit.MainCoroutineRule
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.ui.MyViewModel
import io.mockk.Invocation
import io.mockk.verify
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever

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
    fun `get All Images From Dao, return correct size`() = runBlocking{

        whenever(mockRepository.allImagesFromDao).thenAnswer { flow { emit(listOf(img1, img2)) } }

        mockRepository.allImagesFromDao.collect { item ->
            assertNotNull(item)
            assertEquals(2, item.size)
//            println("title: ${item[0].title}")
        }
    }

    @Test
    fun `get All Images From Dao, return the same content`() = runBlocking{

        whenever(mockRepository.allImagesFromDao).thenAnswer { flow { emit(listOf(img1, img2)) } }

        mockRepository.allImagesFromDao.collect { item ->
            assertNotNull(item)
            assertEquals("image1", item[0].title)
        }
    }


    @Test
    fun `insert data, result must be number of inserted rows`() = runBlocking{
        whenever(mockRepository.insert(img1)).thenReturn(1) // return the number of added rows
        whenever(mockRepository.insert(img1)).thenAnswer { invocation -> println("invocation: ${invocation.getArguments().last()}")}
        var response = whenever(mockRepository.insert(img1)).thenAnswer { invocation -> return@thenAnswer invocation.getArguments().last() }
println("response: ${response.then { i -> i.getArguments().last() }}")
        assertEquals(1, mockRepository.insert(img1))
    }

    @Test
    fun `update data, result must be number of updated rows`() = runBlocking{
        whenever(mockRepository.update(img1)).thenCallRealMethod()// .thenReturn(1) // return the number of updated rows
        assertEquals(1, mockRepository.update(img1))
//        verify (mockRepository, times(1))

//        assertEquals(1, mockRepository.update(img1))
    }

    @Test
    fun `delete data, result must be number of deleted rows`(): Unit = runBlocking{
        whenever(mockRepository.delete(img1)).thenReturn(1) // return the number of deleted rows

        org.mockito.kotlin.verify(mockRepository).delete(img1)
//        assertEquals(1, mockRepository.delete(img1))
    }

    @Test
    fun `delete all data, result must be number of deleted rows`() = runBlocking{
doReturn(false).whenever(mockRepository.deleteAll())
        whenever(mockRepository.deleteAll()).thenReturn(1) // return the number of deleted rows

        assertEquals(1, mockRepository.deleteAll() )
    }
}