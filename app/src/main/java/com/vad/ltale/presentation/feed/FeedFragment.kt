package com.vad.ltale.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vad.ltale.App
import com.vad.ltale.R
import com.vad.ltale.model.Like
import com.vad.ltale.model.PlayView
import com.vad.ltale.model.PostResponse
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.audiohandle.PlayHandler
import com.vad.ltale.model.audiohandle.Player
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LikeViewModel
import com.vad.ltale.presentation.LikeViewModelFactory
import com.vad.ltale.presentation.LoadViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.LikeOnClickListener
import com.vad.ltale.presentation.adapter.PlayOnClickListener
import com.vad.ltale.presentation.adapter.PostAdapter

class FeedFragment : BaseFragment(), PlayOnClickListener, LikeOnClickListener,
    AccountClickListener {

    private val postViewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(
            PostRepository(mainViewModel.getRetrofit())
        )
    }
    private val likeViewModel: LikeViewModel by activityViewModels {
        LikeViewModelFactory(
            LikeRepository(mainViewModel.getRetrofit())
        )
    }
    private val load: FileViewModel by activityViewModels {
        LoadViewModelFactory(
            FileRepository(
                SaveInternalHandle(thisContext),
                (activity?.application as App).database.audioDao(),
                mainViewModel.getRetrofit()
            )
        )
    }
    private lateinit var adapter: PostAdapter
    private lateinit var playHandler: PlayHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById(R.id.feedRecyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        adapter = PostAdapter(load, this, this, this, mainViewModel.getUserDetails().userId)

        postViewModel.getPosts()

        playHandler = PlayHandler(Player(thisContext))

        load.uriAudio.observe(viewLifecycleOwner) {
            playHandler.handle(
                it.first.position,
                it.second,
                it.first.audioAdapter,
                it.first.seekBar,
                it.first.timeTextView
            )
            it.first.progressBar.visibility = View.GONE
        }

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

    override fun onClick(id: Long) {
        val directions = FeedFragmentDirections.actionFeedFragmentToPeopleAccountFragment(id)
        findNavController().navigate(directions)
    }

}