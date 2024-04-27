package com.vad.ltale.presentation.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.FollowRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.pojo.Like
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.model.pojo.User
import com.vad.ltale.presentation.AudioBaseFragment
import com.vad.ltale.presentation.FollowViewModel
import com.vad.ltale.presentation.FollowViewModelFactory
import com.vad.ltale.presentation.LikeViewModel
import com.vad.ltale.presentation.LikeViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import com.vad.ltale.presentation.adapter.LikeOnClickListener

open class AccountBaseFragment : AudioBaseFragment(), LikeOnClickListener {

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

    protected val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(RemoteInstance))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("@##", postViewModel.toString())
        userDetails = RemoteInstance.user
        userId = userDetails.userId
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