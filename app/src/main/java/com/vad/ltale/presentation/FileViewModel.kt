package com.vad.ltale.presentation

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.Image
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import ir.logicbase.livex.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(private val fileRepository: FileRepository) : ViewModel() {

    val uriAudio: SingleLiveEvent<String> = SingleLiveEvent()
    val uploadIcon: MutableLiveData<Resource<Image>> = MutableLiveData()

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun getUri(audio: Audio) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        uriAudio.postValue(fileRepository.getUriByAudio(audio))
    }

    fun getImage(id: Long?, imageViewPost: ImageView) {
        if (id != null) fileRepository.getImage(id, imageViewPost)
    }

    fun getIcon(userId: Long, imageIcon: ShapeableImageView) {
        fileRepository.getIcon(userId, imageIcon)
    }

    fun uploadIcon(context: Context, file: File, userId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

        uploadIcon.postValue(Resource.Loading)

        val compressImage = Compressor.compress(context, file) {
            quality(50)
            size(1_000_000)
        }

        uploadIcon.postValue(fileRepository.uploadIcon(compressImage, userId))
    }

}

@Suppress("UNCHECKED_CAST")
class LoadViewModelFactory(private val repository: FileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(repository) as T
    }
}