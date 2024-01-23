package com.vad.ltale.presentation.account

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.vad.ltale.App
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.FollowRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.Like
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.model.pojo.User
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.FollowViewModel
import com.vad.ltale.presentation.FollowViewModelFactory
import com.vad.ltale.presentation.LikeViewModel
import com.vad.ltale.presentation.LikeViewModelFactory
import com.vad.ltale.presentation.LoadViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import com.vad.ltale.presentation.adapter.LikeOnClickListener

open class AccountBaseFragment : BaseFragment(), LikeOnClickListener {

    protected var userId = -1L
    protected lateinit var userDetails: User

    protected val followViewModel: FollowViewModel by activityViewModels {
        FollowViewModelFactory(FollowRepository(RemoteInstance))
    }

    protected val postViewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(
            PostRepository(RemoteInstance)
        )
    }
    protected val likeViewModel: LikeViewModel by activityViewModels {
        LikeViewModelFactory(
            LikeRepository(RemoteInstance)
        )
    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetails = RemoteInstance.user
        userId = userDetails.userId
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

    override fun onLike(post: PostResponse, position: Int) {

        //like only user who uses app at time
        val like = Like(userId, post.postId)

        if (post.isLiked) {
            likeViewModel.deleteLike(like, position, post)
        } else {
            likeViewModel.addLike(like, position, post)
        }
    }

}