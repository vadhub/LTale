package com.vad.ltale.presentation

import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.vad.ltale.App
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.model.pojo.Audio

open class AudioBaseFragment : BaseFragment() {

    protected val load: FileViewModel by activityViewModels {
        LoadViewModelFactory(
            FileRepository(
                SaveInternalHandle(thisContext),
                (activity?.application as App).database.audioDao(),
                RemoteInstance
            )
        )
    }

    protected val player: ExoPlayer by lazy {
        ExoPlayer.Builder(thisContext).build()
    }

    protected fun prepareAudioHandler(): PlaylistHandler {
        var changePlayItemTemp: () -> Unit = {}

        val play: (audio: Audio, changePlayItem: () -> Unit) -> Unit =
            { audio: Audio, changePlayItem: () -> Unit ->
                load.getUri(audio)
                changePlayItemTemp = changePlayItem
            }

        load.uriAudio.observe(viewLifecycleOwner) {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
            changePlayItemTemp.invoke()
        }

        return PlaylistHandler(player, play)
    }

    protected fun prepareAudioHandleWithoutViewModel(): PlaylistHandler {
        val play: (audio: Audio, changePlayItem: () -> Unit) -> Unit =
            { audio: Audio, changePlayItem: () -> Unit ->
                player.setMediaItem(MediaItem.fromUri(audio.uri))
                player.prepare()
                player.play()
                changePlayItem.invoke()
            }

         return PlaylistHandler(player, play)
    }
}