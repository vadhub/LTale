package com.vad.ltale.presentation.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.FileUtil
import com.vad.ltale.domain.audiohandle.PlayHandler
import com.vad.ltale.domain.audiohandle.Player
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.PostAdapter
import com.vad.ltale.presentation.adapter.RecyclerOnClickListener
import java.io.File


class AccountFragment : BaseFragment(), RecyclerOnClickListener {

    private lateinit var postViewModel: PostViewModel
    private lateinit var playHandler: PlayHandler
    private lateinit var load: FileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val imageIcon: ImageView = view.findViewById(R.id.imageIcon)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val countPost: TextView = view.findViewById(R.id.countPosts)

        val factory = LoadViewModelFactory(mainViewModel.getRetrofit())
        load = ViewModelProvider(this, factory).get(FileViewModel::class.java)

        val factoryMessage = PostViewModelFactory(PostRepository(mainViewModel.getRetrofit()))
        postViewModel = ViewModelProvider(this, factoryMessage).get(PostViewModel::class.java)

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

        val adapter = PostAdapter(load, this)

        postViewModel.posts.observe(viewLifecycleOwner) {
            Log.d("##account", "-------------------------")
            if (it.isNotEmpty()) {
                Log.d("##account", "${it}-------------------------")
                adapter.setPosts(it)
                recyclerView.adapter = adapter
                countPost.text = "${it.size}"
            }
        }

        playHandler = PlayHandler(Player())

        buttonCreateRecord.setOnClickListener { view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment) }
    }

    override fun onResume() {
        super.onResume()
        postViewModel.getPostsByUserId(mainViewModel.getUserDetails().userId)
    }

    override fun onItemClick(position: Int, audio: Audio, playButton: ShapeableImageView, seekBar: SeekBar, parentRecyclerView: RecyclerView) {
        load.getAudioById(audio)
        playHandler.handle(position, playButton, load.uriAudio.value ?: "", seekBar, parentRecyclerView)
    }
}