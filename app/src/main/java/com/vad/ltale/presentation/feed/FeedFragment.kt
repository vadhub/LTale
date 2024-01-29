package com.vad.ltale.presentation.feed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.pojo.Like
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.presentation.AudioBaseFragment
import com.vad.ltale.presentation.LikeViewModel
import com.vad.ltale.presentation.LikeViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.LikeOnClickListener
import com.vad.ltale.presentation.adapter.PostAdapter

class FeedFragment : AudioBaseFragment(), LikeOnClickListener,
    AccountClickListener {

    private val postViewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(
            PostRepository(RemoteInstance)
        )
    }
    private val likeViewModel: LikeViewModel by activityViewModels {
        LikeViewModelFactory(
            LikeRepository(RemoteInstance)
        )
    }

    private lateinit var adapter: PostAdapter

    private var currentUser = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = RemoteInstance.user.userId
        postViewModel.getPosts(currentUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val progressBar: ProgressBar = view.findViewById(R.id.progressBarFeed)
        val recyclerView: RecyclerView = view.findViewById(R.id.feedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        swipeRefreshLayout.setOnRefreshListener {
            postViewModel.clearPosts()
            postViewModel.getPosts(currentUser)
            swipeRefreshLayout.isRefreshing = false
        }

        val onReachEndListener: () -> Unit = {
            postViewModel.getPosts(currentUser)
        }

        adapter = PostAdapter(load, this, this, onReachEndListener, prepareAudioHandler())
        recyclerView.adapter = adapter

        postViewModel.posts.observe(viewLifecycleOwner) {
            adapter.setPosts(it)
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
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
                return true
            }
        })

    }

    override fun onLike(post: PostResponse, position: Int) {

        val like = Like(currentUser, post.postId)

        if (post.isLiked) {
            likeViewModel.deleteLike(like, position, post)
        } else {
            likeViewModel.addLike(like, position, post)
        }
    }

    override fun onClick(id: Long) {
        if (id != currentUser) {
            val directions = FeedFragmentDirections.actionFeedFragmentToPeopleAccountFragment(id)
            findNavController().navigate(directions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
    }

}