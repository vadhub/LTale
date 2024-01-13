package com.vad.ltale.presentation

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.model.Audio
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(private val fileRepository: FileRepository) : ViewModel() {

    val uriAudio: MutableLiveData<String> = MutableLiveData()

    fun getUri(audio: Audio) = viewModelScope.launch {
        uriAudio.postValue(fileRepository.getUriByAudio(audio))
    }

    fun getImage(id: Long?, context: Context?, imageViewPost: ImageView) = viewModelScope.launch {
        if (id != null) fileRepository.getImage(id, context, imageViewPost)
    }

    fun getIcon(userId: Long, context: Context?, imageIcon: ShapeableImageView) = viewModelScope.launch {
        fileRepository.getIcon(userId, context, imageIcon)
    }

    fun uploadIcon(file: File, userId: Long) = viewModelScope.launch {
        fileRepository.uploadIcon(file, userId)
    }

    fun removeAudioById(id: Long) = viewModelScope.launch {
        fileRepository.removeAudioById(id)
    }

}

class LoadViewModelFactory(private val repository: FileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(repository) as T
    }
}