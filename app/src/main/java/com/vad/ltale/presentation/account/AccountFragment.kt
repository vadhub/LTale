package com.vad.ltale.presentation.account

import android.app.Activity
import android.content.Intent
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.App
import com.vad.ltale.R
import com.vad.ltale.data.Like
import com.vad.ltale.data.PlayView
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.local.SaveConfiguration
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.FileUtil
import com.vad.ltale.domain.audiohandle.PlayHandler
import com.vad.ltale.domain.audiohandle.Player
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.AudioAdapter
import com.vad.ltale.presentation.adapter.LikeOnClickListener
import com.vad.ltale.presentation.adapter.PostAdapter
import com.vad.ltale.presentation.adapter.PlayOnClickListener
import okhttp3.internal.notify
import java.io.File


class AccountFragment : BaseFragment(), PlayOnClickListener, LikeOnClickListener {

    private lateinit var postViewModel: PostViewModel
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var playHandler: PlayHandler
    private lateinit var load: FileViewModel
    private lateinit var adapter: PostAdapter
    private lateinit var saveConfiguration: SaveConfiguration;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("##acc", "onCreateView: ")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("##acc", "onViewCreated: ")

        saveConfiguration = SaveConfiguration(thisContext)

        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIcon)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val countPost: TextView = view.findViewById(R.id.countPosts)

        val factory = LoadViewModelFactory(FileRepository((activity?.application as App).database.audioDao(), mainViewModel.getRetrofit()))
        load = ViewModelProvider(this, factory).get(FileViewModel::class.java)

        val factoryPost = PostViewModelFactory(PostRepository(mainViewModel.getRetrofit()))
        postViewModel = ViewModelProvider(this, factoryPost).get(PostViewModel::class.java)

        val factoryLike = LikeViewModelFactory(LikeRepository(mainViewModel.getRetrofit()))
        likeViewModel = ViewModelProvider(this, factoryLike).get(LikeViewModel::class.java)

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val selectedImage = it.data
                imageIcon.setImageURI(selectedImage!!.data)
                load.uploadIcon(File(FileUtil.getPath(selectedImage.data, context)), mainViewModel.getUserDetails().userId)
            }
        }

        load.getIcon(mainViewModel.getUserDetails().userId, context, imageIcon)

        imageIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        username.text = mainViewModel.getUserDetails().username

        adapter = PostAdapter(load, this, this)

        postViewModel.posts.observe(viewLifecycleOwner) {
            Log.d("##account", "-------------------------")
            if (it.isNotEmpty()) {
                Log.d("##account", "${it}-------------------------")
                adapter.setPosts(it)
                recyclerView.adapter = adapter
                countPost.text = "posts: ${it.size}"
            }
        }

        playHandler = PlayHandler(Player(thisContext))

        load.uriAudio.observe(viewLifecycleOwner) {
            playHandler.handle(it.first.position, it.second, it.first.audioAdapter, it.first.seekBar)
            Log.d("!!mv", "onViewCreated: ${it.second}")
            it.first.progressBar.visibility = View.GONE
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

        buttonCreateRecord.setOnClickListener { view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment) }
    }

    override fun onStart() {
        super.onStart()
        Log.d("##acc", "onStart: ")
        postViewModel.getPostsByUserId(mainViewModel.getUserDetails().userId)
    }

    override fun onItemClick(playView: PlayView) {
        playView.progressBar.visibility = View.VISIBLE
        load.getUriByAudio(playView)
    }

    override fun onLike(post: PostResponse, position: Int) {
        val like = Like(mainViewModel.getUserDetails().userId, post.postId)

        if (post.isLiked) {
            likeViewModel.deleteLike(like, position, post)
        } else {
            likeViewModel.addLike(like, position, post)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sign_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.sigh_out) {
            saveConfiguration.saveFirstStart(false)
            findNavController().navigate(R.id.action_accountFragment_to_registrationFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
