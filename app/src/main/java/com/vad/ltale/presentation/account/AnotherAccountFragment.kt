package com.vad.ltale.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.MainActivity
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.databinding.FragmentPeopleAccountBinding
import com.vad.ltale.model.pojo.Follow
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import com.vad.ltale.presentation.adapter.AccountClickListener
import com.vad.ltale.presentation.adapter.PostAdapter

class AnotherAccountFragment : AccountBaseFragment() {

    private var _binding: FragmentPeopleAccountBinding? = null
    private val binding get() = _binding!!

    private var adapter: PostAdapter? = null
    private var followed = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: AnotherAccountFragmentArgs by navArgs()
        followed = args.uid
        userViewModel.getUser(followed)
        postViewModel.getCountOfPostsByUserId(followed)
        postViewModel.getPostsByUserIdPaging(followed, userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val collapseToolbar: CollapsingToolbarLayout = binding.collapseToolbar
        val progressBar: ProgressBar = binding.progressBarAnother
        val imageIcon: ShapeableImageView = binding.imageIconAnother
        val username: TextView = binding.usernameAnother
        val countPost: TextView = binding.countPostsAnother
        val countFollowers: TextView = binding.countFollowersAnother
        val addToFriend: ImageView = binding.addFriend
        val recyclerView: RecyclerView = binding.recyclerItemRecordsAnother
        recyclerView.layoutManager = LinearLayoutManager(thisContext)

        var followers = 0L
        var isSubscribe = false

        val onReachEndListener: () -> Unit = {
            postViewModel.getPostsByUserIdPaging(followed, userId)
        }

        adapter = PostAdapter(load, this, AccountClickListener.EmptyAccountClickListener() , onReachEndListener, prepareAudioHandler())
        recyclerView.adapter = adapter

        followViewModel.checkSubscribe(userId, followed)

        followViewModel.isSubscribe.observe(viewLifecycleOwner) {
            isSubscribe = it
            addToFriend.setImageResource(if (it) R.drawable.baseline_how_to_reg_24 else R.drawable.baseline_person_add_alt_1_24)
        }

        addToFriend.setOnClickListener {
            if (!isSubscribe) {
                followViewModel.subscribe(followers, Follow(userId, followed))
                addToFriend.setImageResource(R.drawable.baseline_how_to_reg_24)
            } else {
                followViewModel.unsubscribe(followers, Follow(userId, followed))
                addToFriend.setImageResource(R.drawable.baseline_person_add_alt_1_24)
            }
        }

        userViewModel.userDetails.observe(viewLifecycleOwner) {
            followViewModel.getSubscribers(it.userId)
            load.getIcon(it.userId, imageIcon)
            username.text = it.username
            (requireActivity() as MainActivity).setActionBarTitle(it.username)
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            collapseToolbar.visibility = View.VISIBLE
        }

        postViewModel.countOfPosts.observe(viewLifecycleOwner) {
            countPost.text = "$it"
        }

        postViewModel.postsByUserId.observe(viewLifecycleOwner) {
            adapter?.setPosts(it)
        }

        followViewModel.countOfSubscribers.observe(viewLifecycleOwner) {
            followers = it
            countFollowers.text = "$it"
        }

        likeViewModel.likeData.observe(viewLifecycleOwner) {
            adapter?.notifyItemChanged(it.first, it.second)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        player.stop()
        adapter = null
    }

}