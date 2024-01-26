package com.vad.ltale.presentation

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.model.pojo.Audio
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import ir.logicbase.livex.SingleLiveEvent
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(private val fileRepository: FileRepository) : ViewModel() {

    val uriAudio: SingleLiveEvent<String> = SingleLiveEvent()

    fun getUri(audio: Audio) = viewModelScope.launch {
        uriAudio.postValue(fileRepository.getUriByAudio(audio))
    }

    fun getImage(id: Long?, imageViewPost: ImageView) {
        if (id != null) fileRepository.getImage(id, imageViewPost)
    }

    fun getIcon(userId: Long, imageIcon: ShapeableImageView) {
        fileRepository.getIcon(userId, imageIcon)
    }

    fun uploadIcon(context: Context, file: File, userId: Long) = viewModelScope.launch {
        val compressImage = Compressor.compress(context, file) {
            quality(50)
        }

        fileRepository.uploadIcon(compressImage, userId)
    }

    fun removeAudioById(id: Long) = viewModelScope.launch {
        fileRepository.removeAudioById(id)
    }

}

@Suppress("UNCHECKED_CAST")
class LoadViewModelFactory(private val repository: FileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(repository) as T
    }
}