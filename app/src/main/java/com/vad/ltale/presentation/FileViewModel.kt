package com.vad.ltale.presentation

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.data.PlayView
import com.vad.ltale.data.repository.FileRepository
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(private val fileRepository: FileRepository) : ViewModel() {

    val uriAudio: MutableLiveData<Pair<PlayView, String>> = MutableLiveData()
    private var uriTemp = ""
    private var localUriTemp = ""

    fun getUriByAudio(playView: PlayView) = viewModelScope.launch {
        if (uriTemp != playView.audio.uri) {
            localUriTemp = fileRepository.getUriByAudio(playView.audio)
            Log.d("!!mv", "getUriByAudio: $localUriTemp")
        }

        uriAudio.postValue(Pair(playView, localUriTemp))
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

}