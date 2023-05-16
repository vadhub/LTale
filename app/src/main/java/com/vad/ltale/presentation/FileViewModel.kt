package com.vad.ltale.presentation

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.data.Audio
import com.vad.ltale.data.repository.FileRepository
import kotlinx.coroutines.launch
import java.io.File


class FileViewModel(private val fileRepository: FileRepository) : ViewModel() {

    val uriAudio: MutableLiveData<String> = MutableLiveData()

    fun getUriByAudio(audio: Audio) = viewModelScope.launch {
        uriAudio.postValue(fileRepository.getUriByAudio(audio))
    }

    fun getImage(id: Int, context: Context?, imageViewPost: ImageView) = viewModelScope.launch {
        fileRepository.getImage(id, context, imageViewPost)
    }

    fun getIcon(userId: Int, context: Context?, imageIcon: ShapeableImageView) = viewModelScope.launch {
        fileRepository.getIcon(userId, context, imageIcon)
    }

    fun uploadIcon(file: File, userId: Int) = viewModelScope.launch {
        fileRepository.uploadIcon(file, userId)
    }

}