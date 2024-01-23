package com.vad.ltale.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.FollowRepository
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.pojo.Follow
import com.vad.ltale.presentation.FollowViewModel
import com.vad.ltale.presentation.FollowViewModelFactory
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import com.vad.ltale.presentation.adapter.PostAdapter

class AnotherAccountFragment : AccountFragment() {

    private val followViewModel: FollowViewModel by activityViewModels {
        FollowViewModelFactory(FollowRepository(RemoteInstance))
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(RemoteInstance))
    }

    private lateinit var adapter: PostAdapter
    private var followed = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: AnotherAccountFragmentArgs by navArgs()
        followed = args.uid
        userViewModel.getUser(followed)
        postViewModel.getCountOfPostsByUserId(followed)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val progressBar: ProgressBar = view.findViewById(R.id.progressBarAnother)
        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIconAnother)
        val username: TextView = view.findViewById(R.id.usernameAnother)
        val countPost: TextView = view.findViewById(R.id.countPostsAnother)
        val countFollowers: TextView = view.findViewById(R.id.countFollowersAnother)
        val addToFriend: ImageView = view.findViewById(R.id.addFriend)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecordsAnother)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        var followers = 0L
        val follower = RemoteInstance.user.userId
        var isSubscribe = false

        val onReachEndListener: () -> Unit = {
            postViewModel.getPostsByUserId(followed, follower)
        }

        adapter = PostAdapter(load, this, this, onReachEndListener, prepareAudioHandler())
        recyclerView.adapter = adapter

        followViewModel.checkSubscribe(follower, followed)

        followViewModel.isSubscribe.observe(viewLifecycleOwner) {
            isSubscribe = it
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

        userViewModel.userDetails.observe(viewLifecycleOwner) {
            followViewModel.getSubscribers(it.userId)
            load.getIcon(it.userId, imageIcon)
            username.text = it.username
            (requireActivity() as MainActivity).setActionBarTitle(it.username)

            postViewModel.getPostsByUserId(it.userId, follower)
        }

        postViewModel.countOfPosts.observe(viewLifecycleOwner) {
            countPost.text = "$it"
        }

        postViewModel.postsByUserId.observe(viewLifecycleOwner) {
            adapter.setPosts(it)
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        followViewModel.countOfSubscribers.observe(viewLifecycleOwner) {
            followers = it
            countFollowers.text = "$it"
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

    }

}