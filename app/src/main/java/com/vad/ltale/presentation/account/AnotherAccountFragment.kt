package com.vad.ltale.presentation.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.Like
import com.vad.ltale.model.PostResponse
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import com.vad.ltale.presentation.adapter.PostAdapter

class AnotherAccountFragment: AccountFragment(), HandleResponse {

    private lateinit var userViewModel: UserViewModel
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

        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        val args: AnotherAccountFragmentArgs by navArgs()

        userViewModel.getUser(args.uid)

        adapter = PostAdapter(load, this, this, mainViewModel.getUserDetails().userId, prepareAudioHandler())

        userViewModel.userDetails.observe(viewLifecycleOwner) {
            userDetails = it

            load.getIcon(userDetails.userId, context, imageIcon)
            username.text = userDetails.username

            postViewModel.getPostsByUserId(userDetails.userId)
        }

        postViewModel.posts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setPosts(it)
                recyclerView.adapter = adapter
                countPost.text = "${it.size}"
            }
        }

        countFollowers.text = "0"

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

    override fun error(e: String) {
        Log.d("AnotherAccount", "error: $e")
        Toast.makeText(thisContext, "We can`t open this account", Toast.LENGTH_SHORT).show()
    }

    override fun success() {}
}