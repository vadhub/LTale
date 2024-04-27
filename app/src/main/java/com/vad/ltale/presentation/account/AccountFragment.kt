package com.vad.ltale.presentation.account

import android.net.Uri
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
import androidx.fragment.app.activityViewModels
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
import com.google.android.material.snackbar.Snackbar
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.repository.LimitRepository
import com.vad.ltale.databinding.FragmentAccountBinding
import com.vad.ltale.model.pojo.Limit
import com.vad.ltale.presentation.LimitViewModel
import com.vad.ltale.presentation.LimitViewModelFactory
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.PostAdapter
import java.io.File
import java.sql.Date

open class AccountFragment : AccountBaseFragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    val username: TextView by lazy {  binding.usernameTextView }
    private lateinit var adapter: PostAdapter
    private lateinit var bottomMenuActivity: BottomNavigationView

    private val limitViewModel: LimitViewModel by activityViewModels {
        LimitViewModelFactory(
            LimitRepository(RemoteInstance)
        )
    }

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

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar: ProgressBar = binding.progressBarAccount
        val progressBarPost: ProgressBar = binding.progressBarLoadingPost
        val progressBarIcon: ProgressBar = binding.progressBarIcon
        val countPost: TextView = binding.countPosts
        val countFollowers: TextView = binding.countFollowers
        val buttonCreateRecord: FloatingActionButton = binding.createRecordButton
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefresh
        val recyclerView: RecyclerView = binding.recyclerItemRecords
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
            Toast.makeText(
                thisContext,
                getString(R.string.wait_for_the_post_to_load),
                Toast.LENGTH_SHORT
            ).show()
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

        load.getIcon(userId, binding.imageIcon)
        username.text = userDetails.username
        countFollowers.text = "0"

        buttonCreateRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment)
        }

        binding.imageIcon.setOnClickListener {
            startCrop()
        }

        load.uploadIcon.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressBarIcon.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    postViewModel.getPostsByUserId(userId, userId)
                    load.invalidate(userId, binding.imageIcon)
                    progressBarIcon.visibility = View.GONE
                }

                is Resource.Failure -> {
                    Toast.makeText(
                        thisContext,
                        getString(R.string.error_loading_icon),
                        Toast.LENGTH_SHORT
                    ).show()
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
                    Snackbar.make(
                        buttonCreateRecord,
                        getString(R.string.post_fail_loading),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is Resource.Success -> {
                    postViewModel.getCountOfPostsByUserId(userId)
                    postViewModel.getPostsByUserId(userId, userId)
                    progressBarPost.visibility = View.GONE
                    buttonCreateRecord.visibility = View.VISIBLE
                    limitViewModel.updateTime(
                        Limit(
                            limitViewModel.limit.value!!.id,
                            userId,
                            limitViewModel.time,
                            "${Date(System.currentTimeMillis())}"
                        )
                    )
                    Snackbar.make(
                        buttonCreateRecord,
                        getString(R.string.post_load),
                        Snackbar.LENGTH_SHORT
                    ).show()

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

                    Snackbar.make(
                        buttonCreateRecord,
                        resources.getString(R.string.post_removed),
                        Snackbar.LENGTH_SHORT
                    ).show()

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
            val uriContent = result.getUriFilePath(thisContext, true)

            uriContent?.let {
                binding.imageIcon.setImageURI(Uri.parse(uriContent))
                load.uploadIcon(thisContext, File(uriContent), userId)
            }
        } else {
            Toast.makeText(thisContext, getString(R.string.can_t_load_image), Toast.LENGTH_SHORT)
                .show()
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

        when (item.itemId) {
            R.id.sigh_out -> {
                configuration.clear()
                bottomMenuActivity.visibility = View.GONE
                findNavController().navigate(R.id.action_accountFragment_to_registrationFragment)
                return true
            }

            R.id.change_nik -> {
                createSettingsDialog()
                return true
            }

            R.id.change_photo -> {
                startCrop()
                return true
            }
            else -> return false
        }
    }

    private fun createSettingsDialog() {
        val changeNick: (new: String) -> Unit = {
            userViewModel.changeUsername(it, userId)
            configuration.saveLogin(it)
            RemoteInstance.setUser(userDetails.copy(username=it))
            username.text = it
        }
        val settingsDialog = SettingsAccount(changeNick)
        settingsDialog.show(parentFragmentManager, "SettingsDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        player.stop()
    }
}
