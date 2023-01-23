package com.example.room_mvvm_junit.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.repositories.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(private val repository: BaseRepository) : ViewModel() {

    var inputName = MutableLiveData<String>()
    var imageURL = MutableLiveData<String>()
    var saveButtonText = MutableLiveData<String>()
    var deleteButtonText = MutableLiveData<String>()
    private var statusMessage = MutableLiveData<String>()
    var stmessage: MutableLiveData<String> = statusMessage
    private var isUpdate = false
    private lateinit var imageList: ImageDataModel
    val state = MutableStateFlow<State>(State.START)


    init {
        saveButtonText.value = "Save"
        deleteButtonText.value = "Delete All"
        inputName.value = ""
        imageURL.value = ""
    }


    fun getSavedImages() = liveData {
        try {
//            state.value = State.LOADING
            repository.allImagesFromDao.collect{ emit(it)

//                state.value = State.SUCCESS(it)
            }
        }catch (e:Exception){
//            state.value = State.FAILURE(e.localizedMessage!!)
            statusMessage.value = "Error occurred"
            Log.e("MSC", "getSavedImages() Error occurred")
        }
    }


    fun onSaveClick() {
        val title = inputName.value!!.toString()
        val image = imageURL.value!!.toString()
        println()
        if (isUpdate && validateInput(title, image)) {
            imageList.title = inputName.value!!
            imageList.imageURL = imageURL.value!!
            update(imageList)
            resetView()
        } else if (validateInput(title, image)) {
            insert(ImageDataModel(0, image, title))
            resetView()
        }
    }

    fun resetView() {
        inputName.value = ""
        imageURL.value = ""
    }

    fun validateInput(imageTitle: String?, imageUrl: String?): Boolean {
        if (imageTitle.isNullOrEmpty()) {
            statusMessage.value = "Please enter correct title."
            return false
        } else if (imageUrl.isNullOrEmpty()) {
            statusMessage.value = "Please select image."
            return false
        }
        return true
    }

    fun insert(imageDataModel: ImageDataModel) = viewModelScope.launch {
        if (imageDataModel.imageURL.isEmpty() || imageDataModel.title.isEmpty()) {
            statusMessage.value = "The fields must not be empty"
            return@launch
        }
        val result = repository.insert(imageDataModel)
        if (result > -1) statusMessage.value = "Image successfully inserted"
        else {
            statusMessage.value = "Error occurred"
            Log.e("MSC", "insert() Error occurred")
        }
    }

    fun update(imageDataModel: ImageDataModel) = viewModelScope.launch {
        if (imageDataModel.imageURL.isEmpty() || imageDataModel.title.isEmpty()) {
            statusMessage.value = "The fields must not be empty"
            return@launch
        }
        val result = repository.update(imageList)
        if (result > 0) {
            isUpdate = false
            saveButtonText.value = "Save"
            deleteButtonText.value = "Delete All"
            statusMessage.value = "$result image updated successfully"
        } else statusMessage.value = "Error occurred"
    }

    fun onDeleteClick() {
        if (isUpdate) deleteItem(imageList)
        else deleteAll()
    }

    fun deleteItem(imageDataModel: ImageDataModel) = viewModelScope.launch {
        val result = repository.delete(imageDataModel)
        if (result > 0) {
            resetView()
            isUpdate = false
            saveButtonText.value = "Save"
            deleteButtonText.value = "Delete All"
            statusMessage.value = "$result Row deleted successfully"
        } else statusMessage.value = "Error occurred"
    }

    fun deleteAll() = viewModelScope.launch {
        val result = repository.deleteAll()
        if (result > 0) {
            resetView()
            statusMessage.value = "$result values deleted successfully"
        } else statusMessage.value = "Error occurred"
    }


    fun onSelectedItemClick(imageDataModel: ImageDataModel) {
        inputName.value = imageDataModel.title
        imageURL.value = imageDataModel.imageURL
        imageList = imageDataModel
        isUpdate = true
        saveButtonText.value = "Update"
        deleteButtonText.value = "Delete"
    }


    fun onImageClick() {
        statusMessage.value = "loadImage"
    }

}

sealed class State {
    object START : State()
    object LOADING : State()
    data class SUCCESS(val images: List<ImageDataModel>) : State()
    data class FAILURE(val message: String) : State()
}