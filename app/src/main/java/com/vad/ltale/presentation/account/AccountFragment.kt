package com.vad.ltale.presentation.account

import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
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
import com.vad.ltale.data.User
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.FileUtil
import com.vad.ltale.domain.audiohandle.PlayHandler
import com.vad.ltale.domain.audiohandle.Player
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.LikeOnClickListener
import com.vad.ltale.presentation.adapter.PostAdapter
import com.vad.ltale.presentation.adapter.PlayOnClickListener
import java.io.File

open class AccountFragment : BaseFragment(), PlayOnClickListener, LikeOnClickListener,
    AccountClickListener {

    protected val postViewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(
            PostRepository(mainViewModel.getRetrofit())
        )
    }
    protected val likeViewModel: LikeViewModel by activityViewModels {
        LikeViewModelFactory(
            LikeRepository(mainViewModel.getRetrofit())
        )
    }
    protected val load: FileViewModel by activityViewModels {
        LoadViewModelFactory(
            FileRepository(
                SaveInternalHandle(thisContext),
                (activity?.application as App).database.audioDao(),
                mainViewModel.getRetrofit()
            )
        )
    }

    protected lateinit var playHandler: PlayHandler
    protected lateinit var adapter: PostAdapter
    protected lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetails = mainViewModel.getUserDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("##acc", "onViewCreated: ")

        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIcon)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val countPost: TextView = view.findViewById(R.id.countPosts)
        val countFollowers: TextView = view.findViewById(R.id.countFollowers)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    val selectedImage = it.data
                    imageIcon.setImageURI(selectedImage!!.data)
                    load.uploadIcon(
                        File(FileUtil.getPath(selectedImage.data, context)),
                        userDetails.userId
                    )
                }
            }

        load.getIcon(userDetails.userId, context, imageIcon)

        imageIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        username.text = userDetails.username

        adapter = PostAdapter(load, this, this, this, mainViewModel.getUserDetails().userId)

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
            playHandler.handle(
                it.first.position,
                it.second,
                it.first.audioAdapter,
                it.first.seekBar,
                it.first.timeTextView
            )
            it.first.progressBar.visibility = View.GONE
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.first, it.second)
        }

        postViewModel.getPostsByUserId(userDetails.userId)

        buttonCreateRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sign_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.sigh_out) {
            configuration.saveFirstStart(false)
            findNavController().navigate(R.id.action_accountFragment_to_registrationFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(id: Long) {

    }
}
