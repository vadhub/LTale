package com.vad.ltale.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.Like
import com.vad.ltale.model.PlayView
import com.vad.ltale.model.PostResponse
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.audiohandle.PlayHandler
import com.vad.ltale.model.audiohandle.Player
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import com.vad.ltale.presentation.adapter.PostAdapter

class PeopleAccountFragment : AccountFragment(), HandleResponse {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIconPeople)
        val username: TextView = view.findViewById(R.id.usernameTextViewPeople)
        val countPost: TextView = view.findViewById(R.id.countPostsPeople)
        val countFollowers: TextView = view.findViewById(R.id.countFollowersPeople)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecordsPeople)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        val adapter = PostAdapter(load, this, this, this, mainViewModel.getUserDetails().userId)

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        val args: PeopleAccountFragmentArgs by navArgs()

        userViewModel.getUser(args.userId)

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
                countPost.text = "posts: ${it.size}"
            }
        }

        countFollowers.text = "followers: 0"

        playHandler = PlayHandler(Player(thisContext))

        load.uriAudio.observe(viewLifecycleOwner) {
            playHandler.handle(it.first.position, it.second, it.first.audioAdapter, it.first.seekBar, it.first.timeTextView)
            it.first.progressBar.visibility = View.GONE
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

    }

    override fun onItemClick(playView: PlayView) {
        playView.progressBar.visibility = View.VISIBLE
        load.getUriByAudio(playView)
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

    override fun error() {

    }

    override fun success() {

    }
}