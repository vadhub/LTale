package com.vad.ltale.presentation.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.model.FileUtil
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.PostAdapter
import java.io.File

open class AccountFragment : AccountBaseFragment() {

    private lateinit var adapter: PostAdapter
    private lateinit var bottomMenuActivity: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postViewModel.getCountOfPostsByUserId(userId)
        postViewModel.getPostsByUserId(userId, userId)
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
        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIcon)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val countPost: TextView = view.findViewById(R.id.countPosts)
        val countFollowers: TextView = view.findViewById(R.id.countFollowers)
        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        progressBarPost.setOnClickListener {
            Toast.makeText(thisContext, getString(R.string.wait_for_the_post_to_load), Toast.LENGTH_SHORT).show()
        }

        swipeRefreshLayout.setOnRefreshListener {
            postViewModel.clearPostsOfUSer()
            postViewModel.getPostsByUserId(userId, userId)
            swipeRefreshLayout.isRefreshing = false
        }

        val onReachEndListener: () -> Unit = {
            postViewModel.getPostsByUserId(userId, userId)
        }

        adapter = PostAdapter(
            load,
            this,
            AccountClickListener.EmptyAccountClickListener(),
            onReachEndListener,
            prepareAudioHandler()
        )
        recyclerView.adapter = adapter

        load.getIcon(userId, imageIcon)
        username.text = userDetails.username
        countFollowers.text = "0"

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    val selectedImage = it.data
                    imageIcon.setImageURI(selectedImage!!.data)
                    load.uploadIcon(
                        thisContext,
                        File(FileUtil.getPath(selectedImage.data, context)),
                        userId
                    )
                }
            }

        buttonCreateRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment)
        }

        imageIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
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
                    progressBarPost.visibility = View.GONE
                    buttonCreateRecord.visibility = View.VISIBLE
                    Snackbar.make(buttonCreateRecord, getString(R.string.post_load), Snackbar.LENGTH_SHORT).show()
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
