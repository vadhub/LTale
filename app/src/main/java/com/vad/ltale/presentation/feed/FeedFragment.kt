package com.vad.ltale.presentation.feed

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vad.ltale.App
import com.vad.ltale.R
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.Like
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LikeViewModel
import com.vad.ltale.presentation.LikeViewModelFactory
import com.vad.ltale.presentation.LoadViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.LikeOnClickListener
import com.vad.ltale.presentation.adapter.PostAdapter

class FeedFragment : BaseFragment(), LikeOnClickListener,
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

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(thisContext).build()
    }

    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById(R.id.feedRecyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        var changePlayItemTemp: () -> Unit = {}
        val onReachEndListener: () -> Unit = {
            postViewModel.getPosts(mainViewModel.getUserDetails().userId)
        }

        val play: (audio: Audio, changePlayItem: () -> Unit) -> Unit =
            { audio: Audio, changePlayItem: () -> Unit ->
                load.getUri(audio)
                changePlayItemTemp = changePlayItem
            }

        val playlistHandler = PlaylistHandler(player, play)

        adapter = PostAdapter(load, this, this, onReachEndListener, playlistHandler)
        recyclerView.adapter = adapter

        postViewModel.getPosts(mainViewModel.getUserDetails().userId)

        load.uriAudio.observe(viewLifecycleOwner) {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
            changePlayItemTemp.invoke()
        }

        postViewModel.posts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setPosts(it)
            }
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_search_view, menu)

        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(Color.WHITE)

        searchView.isIconified = true
        searchView.queryHint = getString(R.string.searching)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    postViewModel.getPostsByText(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("#ddddd1", "111222111")
                return true
            }
        })

    }

    override fun onLike(post: PostResponse, position: Int) {

        val like = Like(mainViewModel.getUserDetails().userId, post.postId)

        if (post.isLiked) {
            likeViewModel.deleteLike(like, position, post)
        } else {
            likeViewModel.addLike(like, position, post)
        }
    }

    override fun onClick(id: Long) {
        val directions = FeedFragmentDirections.actionFeedFragmentToPeopleAccountFragment(id)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
        player.stop()
    }

}