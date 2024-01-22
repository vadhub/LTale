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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.App
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.LikeRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.FileUtil
import com.vad.ltale.model.pojo.Like
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.model.pojo.User
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
import java.io.File

open class AccountFragment : BaseFragment(), LikeOnClickListener,
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

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(thisContext).build()
    }

    private lateinit var adapter: PostAdapter
    private lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetails = mainViewModel.getUserDetails()
        postViewModel.getCountOfPostsByUserId(userDetails.userId)
        postViewModel.getPostsByUserId(userDetails.userId, userDetails.userId)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).bottomMenu.visibility = View.VISIBLE
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBarAccount)
        val imageIcon: ShapeableImageView = view.findViewById(R.id.imageIcon)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val countPost: TextView = view.findViewById(R.id.countPosts)
        val countFollowers: TextView = view.findViewById(R.id.countFollowers)
        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        swipeRefreshLayout.setOnRefreshListener {
            postViewModel.clearPostsOfUSer()
            postViewModel.getPostsByUserId(userDetails.userId, userDetails.userId)
            swipeRefreshLayout.isRefreshing = false
        }

        val onReachEndListener: () -> Unit = {
            postViewModel.getPostsByUserId(userDetails.userId, userDetails.userId)
        }

        adapter = PostAdapter(load, this, this, onReachEndListener, prepareAudioHandler())
        recyclerView.adapter = adapter

        load.getIcon(userDetails.userId, imageIcon)
        username.text = userDetails.username
        countFollowers.text = "0"

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

        buttonCreateRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment)
        }

        imageIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
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

    }

    protected fun prepareAudioHandler(): PlaylistHandler {
        var changePlayItemTemp: () -> Unit = {}

        val play: (audio: Audio, changePlayItem: () -> Unit) -> Unit =
            { audio: Audio, changePlayItem: () -> Unit ->
                load.getUri(audio)
                changePlayItemTemp = changePlayItem
            }

        load.uriAudio.observe(viewLifecycleOwner) {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
            changePlayItemTemp.invoke()
        }

        return PlaylistHandler(player, play)
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
            configuration.clear()
            findNavController().navigate(R.id.action_accountFragment_to_registrationFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(id: Long) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
        player.release()
    }
}
