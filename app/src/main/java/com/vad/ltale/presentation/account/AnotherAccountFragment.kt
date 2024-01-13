package com.vad.ltale.presentation.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.repository.FollowRepository
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.Follow
import com.vad.ltale.model.Like
import com.vad.ltale.model.PostResponse
import com.vad.ltale.presentation.FollowViewModel
import com.vad.ltale.presentation.FollowViewModelFactory
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import com.vad.ltale.presentation.adapter.PostAdapter

class AnotherAccountFragment : AccountFragment() {

    private val followViewModel: FollowViewModel by activityViewModels {
        FollowViewModelFactory(FollowRepository(mainViewModel.getRetrofit()))
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()))
    }

    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIconAnother)
        val username: TextView = view.findViewById(R.id.usernameAnother)
        val countPost: TextView = view.findViewById(R.id.countPostsAnother)
        val countFollowers: TextView = view.findViewById(R.id.countFollowersAnother)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecordsAnother)
        val addToFriend: ImageView = view.findViewById(R.id.addFriend)
        var followers = 0L

        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        val args: AnotherAccountFragmentArgs by navArgs()

        val follower = mainViewModel.getUserDetails().userId
        val followed = args.uid
        var isSubscribe = false

        userViewModel.getUser(followed)
        followViewModel.checkSubscribe(follower, followed)

        followViewModel.isSubscribe.observe(viewLifecycleOwner) {
            isSubscribe = it
            Log.d("!", "onViewCreated: $it")
            addToFriend.setImageResource(if (it) R.drawable.baseline_how_to_reg_24 else R.drawable.baseline_person_add_alt_1_24)
        }

        addToFriend.setOnClickListener {
            if (!isSubscribe) {
                followViewModel.subscribe(followers, Follow(follower, followed))
                addToFriend.setImageResource(R.drawable.baseline_how_to_reg_24)
            } else {
                followViewModel.unsubscribe(followers, Follow(follower, followed))
                addToFriend.setImageResource(R.drawable.baseline_person_add_alt_1_24)
            }
        }

        adapter = PostAdapter(
            load,
            this,
            this,
            follower,
            prepareAudioHandler()
        )

        userViewModel.userDetails.observe(viewLifecycleOwner) {

            followViewModel.getSubscribers(it.userId)
            load.getIcon(it.userId, context, imageIcon)
            username.text = it.username
            (requireActivity() as MainActivity).setActionBarTitle(it.username)

            postViewModel.getPostsByUserId(it.userId, follower, 0)
        }

        postViewModel.posts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setPosts(it)
                recyclerView.adapter = adapter
                countPost.text = "${it.size}"
            }
        }

        followViewModel.mutableLiveData.observe(viewLifecycleOwner) {
            followers = it
            countFollowers.text = "$it"
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

    }

    override fun onLike(post: PostResponse, position: Int) {

        //like only user who uses app at time
        val like = Like(mainViewModel.getUserDetails().userId, post.postId)

        if (post.isLiked) {
            likeViewModel.deleteLike(like, position, post)
        } else {
            likeViewModel.addLike(like, position, post)
        }
    }

}