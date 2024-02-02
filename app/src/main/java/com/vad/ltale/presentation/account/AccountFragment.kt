package com.vad.ltale.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.PostAdapter
import java.io.File

open class AccountFragment : AccountBaseFragment() {

    private lateinit var adapter: PostAdapter
    private lateinit var bottomMenuActivity: BottomNavigationView
    private lateinit var imageIcon: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postViewModel.getCountOfPostsByUserId(userId)
        postViewModel.getPostsByUserIdPaging(userId, userId)
        followViewModel.getSubscribers(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomMenuActivity = (requireActivity() as MainActivity).bottomMenu
        bottomMenuActivity.visibility = View.VISIBLE
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBarAccount)
        val progressBarPost: ProgressBar = view.findViewById(R.id.progressBarLoadingPost)
        val progressBarIcon: ProgressBar = view.findViewById(R.id.progressBarIcon)
        imageIcon = view.findViewById(R.id.imageIcon)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val countPost: TextView = view.findViewById(R.id.countPosts)
        val countFollowers: TextView = view.findViewById(R.id.countFollowers)
        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val itemOnClickListener: (idPost: Long, view: View) -> Unit = { idPost, mView ->
            val popupMenu = PopupMenu(thisContext, mView)
            popupMenu.menuInflater.inflate(R.menu.menu_more, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                postViewModel.removePost(idPost)
                true
            }

            popupMenu.show()
        }

        progressBarPost.setOnClickListener {
            Toast.makeText(thisContext, getString(R.string.wait_for_the_post_to_load), Toast.LENGTH_SHORT).show()
        }

        swipeRefreshLayout.setOnRefreshListener {
            postViewModel.getPostsByUserId(userId, userId)
            swipeRefreshLayout.isRefreshing = false
        }

        val onReachEndListener: () -> Unit = {
            postViewModel.getPostsByUserIdPaging(userId, userId)
        }

        adapter = PostAdapter(
            load,
            this,
            AccountClickListener.EmptyAccountClickListener(),
            onReachEndListener,
            prepareAudioHandler()
        )

        adapter.itemClickListener = itemOnClickListener

        recyclerView.adapter = adapter

        load.getIcon(userId, imageIcon)
        username.text = userDetails.username
        countFollowers.text = "0"

        buttonCreateRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment)
        }

        imageIcon.setOnClickListener {
            startCrop()
        }

        load.uploadIcon.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {progressBarIcon.visibility = View.VISIBLE}

                is Resource.Success -> {
                    postViewModel.getPostsByUserId(userId, userId)
                    load.invalidate(userId, imageIcon)
                    progressBarIcon.visibility = View.GONE
                }
                is Resource.Failure -> {
                    Toast.makeText(thisContext, getString(R.string.error_loading_icon), Toast.LENGTH_SHORT).show()
                    progressBarIcon.visibility = View.GONE
                }
            }
        }

        postViewModel.postResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressBarPost.visibility = View.VISIBLE
                    buttonCreateRecord.visibility = View.GONE
                }

                is Resource.Failure -> {
                    progressBarPost.visibility = View.GONE
                    buttonCreateRecord.visibility = View.VISIBLE
                    Snackbar.make(buttonCreateRecord, getString(R.string.post_fail_loading), Snackbar.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    postViewModel.getCountOfPostsByUserId(userId)
                    postViewModel.getPostsByUserId(userId, userId)
                    progressBarPost.visibility = View.GONE
                    buttonCreateRecord.visibility = View.VISIBLE

                    Snackbar.make(buttonCreateRecord, getString(R.string.post_load), Snackbar.LENGTH_SHORT).show()

                }
            }
        }

        postViewModel.postDelete.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressBarPost.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                is Resource.Failure -> {
                    progressBarPost.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    postViewModel.getPostsByUserId(userId, userId)
                    postViewModel.getCountOfPostsByUserId(userId)
                    progressBarPost.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    Snackbar.make(buttonCreateRecord, resources.getString(R.string.post_removed), Snackbar.LENGTH_SHORT).show()

                }
            }
        }

        postViewModel.countOfPosts.observe(viewLifecycleOwner) {
            countPost.text = "$it"
        }

        postViewModel.postsByUserId.observe(viewLifecycleOwner) {
            recyclerView.visibility = View.VISIBLE
            adapter.setPosts(it)
            progressBar.visibility = View.GONE
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

        followViewModel.countOfSubscribers.observe(viewLifecycleOwner) {
            countFollowers.text = "$it"
        }

    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent

            uriContent?.let {
                imageIcon.setImageURI(uriContent)
                load.uploadIcon(thisContext, File(uriContent.encodedPath), userId)
            }
        } else {
            Toast.makeText(thisContext, getString(R.string.can_t_load_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCrop() {
        cropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeGallery = true,
                    imageSourceIncludeCamera = true
                ),
            ),
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sign_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.sigh_out) {
            configuration.clear()
            bottomMenuActivity.visibility = View.GONE
            findNavController().navigate(R.id.action_accountFragment_to_registrationFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
    }
}
