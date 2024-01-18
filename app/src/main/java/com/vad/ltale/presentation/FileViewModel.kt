package com.vad.ltale.presentation

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Callback
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.CacheIcon
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(private val fileRepository: FileRepository) : ViewModel(), Callback {

    val uriAudio: MutableLiveData<String> = MutableLiveData()
    private val cacheIcon = CacheIcon()

    private val userIdAndImageView: MutableLiveData<Pair<Long, ImageView>> = MutableLiveData()

    fun getUri(audio: Audio) = viewModelScope.launch {
        uriAudio.postValue(fileRepository.getUriByAudio(audio))
    }

    fun getImage(id: Long?, imageViewPost: ImageView) {
        if (id != null) fileRepository.getImage(id, imageViewPost.context, imageViewPost)
    }

    fun getIcon(userId: Long, imageIcon: ShapeableImageView) {
        val icon = cacheIcon.getImage(userId)

        if (icon == null) {
            fileRepository.getIcon(userId, imageIcon.context, imageIcon, this@FileViewModel)
            userIdAndImageView.value = Pair(userId, imageIcon)
        } else {
            imageIcon.setImageDrawable(icon)
        }

        cacheIcon.printIcons()
    }

    fun uploadIcon(file: File, userId: Long) = viewModelScope.launch {
        fileRepository.uploadIcon(file, userId)
    }

    fun removeAudioById(id: Long) = viewModelScope.launch {
        fileRepository.removeAudioById(id)
    }

    override fun onSuccess() {

        val idUser = userIdAndImageView.value?.first
        val icon = userIdAndImageView.value?.second?.drawable

        if (icon != null && idUser != null) {
            cacheIcon.setIcon(idUser, icon)
        }
    }

    override fun onError(e: Exception?) {

    }

}

class LoadViewModelFactory(private val repository: FileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(repository) as T
    }
}