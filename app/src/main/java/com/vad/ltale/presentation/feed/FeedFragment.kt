package com.vad.ltale.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vad.ltale.App
import com.vad.ltale.R
import com.vad.ltale.data.Like
import com.vad.ltale.data.PlayView
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LikeViewModel
import com.vad.ltale.presentation.LikeViewModelFactory
import com.vad.ltale.presentation.LoadViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import com.vad.ltale.presentation.adapter.LikeOnClickListener
import com.vad.ltale.presentation.adapter.PlayOnClickListener
import com.vad.ltale.presentation.adapter.PostAdapter


class FeedFragment : BaseFragment(), PlayOnClickListener, LikeOnClickListener {

    private lateinit var postViewModel: PostViewModel
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var load: FileViewModel
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById(R.id.feedRecyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        val factoryPost = PostViewModelFactory(PostRepository(mainViewModel.getRetrofit()))
        postViewModel = ViewModelProvider(this, factoryPost).get(PostViewModel::class.java)

        val factoryLike = LikeViewModelFactory(LikeRepository(mainViewModel.getRetrofit()))
        likeViewModel = ViewModelProvider(this, factoryLike).get(LikeViewModel::class.java)

        val factory = LoadViewModelFactory(FileRepository((activity?.application as App).database.audioDao(), mainViewModel.getRetrofit()))
        load = ViewModelProvider(this, factory).get(FileViewModel::class.java)

        adapter = PostAdapter(load, this, this)

        postViewModel.getPosts()

        postViewModel.posts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setPosts(it)
                recyclerView.adapter = adapter
            }
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

    }

    override fun onLike(post: PostResponse, position: Int) {

        val like = Like(mainViewModel.getUserDetails().userId, post.postId)

        if (post.isLiked) {
            likeViewModel.deleteLike(like, position, post)
        } else {
            likeViewModel.addLike(like, position, post)
        }
    }

    override fun onItemClick(playView: PlayView) {
        playView.progressBar.visibility = View.VISIBLE
        load.getUriByAudio(playView)
    }

}