package com.vad.ltale.presentation.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.FileUtil
import com.vad.ltale.domain.Supplier
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.PostAdapter
import java.io.File


class AccountFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

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
        val load: FileViewModel = ViewModelProvider(this, factory).get(FileViewModel::class.java)

        val factoryMessage = PostViewModelFactory(PostRepository(mainViewModel.getRetrofit()))
        val postViewModel = ViewModelProvider(this, factoryMessage).get(PostViewModel::class.java)

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val selectedImage = it.data
                imageIcon.setImageURI(selectedImage!!.data)
                load.uploadIcon(File(FileUtil.getPath(selectedImage.data, context)), mainViewModel.getUserDetails().userId)
            }
        }

        load.getIcon(mainViewModel.getUserDetails().userId, context)!!.error(R.drawable.ic_launcher_foreground).into(imageIcon)

        imageIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        username.text = mainViewModel.getUserDetails().username

        val adapter = PostAdapter(load)

        postViewModel.getPostsByUserId(mainViewModel.getUserDetails().userId)
        postViewModel.posts.observe(viewLifecycleOwner) {
            adapter.setPosts(it)
            recyclerView.adapter = adapter
            countPost.text = "${it.size}"
        }

        buttonCreateRecord.setOnClickListener { view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment) }
    }
}