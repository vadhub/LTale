package com.vad.ltale.presentation.feed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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
import com.vad.ltale.databinding.FragmentFeedBinding
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
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdRequest

class FeedFragment : AudioBaseFragment(), LikeOnClickListener, AccountClickListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mBanner = binding.adView
        mBanner.setAdUnitId("R-M-9155942-1") // demo-banner-yandex
        mBanner.setAdSize(BannerAdSize.fixedSize(thisContext, 300, 70))
        val adRequest: AdRequest = AdRequest.Builder().build()
        mBanner.loadAd(adRequest)

        val progressBar: ProgressBar = binding.progressBarFeed
        val recyclerView: RecyclerView = binding.feedRecyclerView
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

        val reportClickListener: ((idPost: Long) -> Unit) = {
            val directions = FeedFragmentDirections.actionFeedFragmentToReportComplaintFragment(it)
            findNavController().navigate(directions)
        }

        adapter = PostAdapter(load, this, this, onReachEndListener, prepareAudioHandler())
        adapter.reportClickListener = reportClickListener
        recyclerView.adapter = adapter

        postViewModel.posts.observe(viewLifecycleOwner) {
            adapter.setPosts(it)
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        postViewModel.posts2.observe(viewLifecycleOwner) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            adapter.setPosts(it)
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

        postViewModel.postComplaint.observe(viewLifecycleOwner) {
            Toast.makeText(thisContext, it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_search_view, menu)

        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        val searchText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.WHITE)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.byNew -> {
                postViewModel.setSortType(1)
                true
            }

            R.id.byOld -> {
                postViewModel.setSortType(2)
                true
            }

            R.id.byPopular -> {
                postViewModel.setSortType(0)
                true
            }

            else -> false
        }
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
        _binding = null
        player.stop()
    }

}