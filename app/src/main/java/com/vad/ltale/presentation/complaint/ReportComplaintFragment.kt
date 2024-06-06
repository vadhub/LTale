package com.vad.ltale.presentation.complaint

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.vad.ltale.App
import com.vad.ltale.R
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.FileRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.databinding.FragmentReportComplaintBinding
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LoadViewModelFactory
import com.vad.ltale.presentation.PostViewModel
import com.vad.ltale.presentation.PostViewModelFactory
import java.text.SimpleDateFormat

class ReportComplaintFragment : BaseFragment() {

    private var _binding: FragmentReportComplaintBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(
            PostRepository(RemoteInstance)
        )
    }

    private val load: FileViewModel by activityViewModels {
        LoadViewModelFactory(
            FileRepository(
                SaveInternalHandle(thisContext),
                (activity?.application as App).database.audioDao(),
                RemoteInstance
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonReport = binding.report

        val spam = binding.spam
        val candid = binding.candid
        val deception = binding.deception
        val prohibited = binding.prohibitedGoods
        val violence = binding.violence

        val nik = binding.textViewNikNameReport
        val icon = binding.imageIconPostReport
        val image = binding.imageViewPostReport
        val hashtag = binding.textViewHashtagReport
        val date = binding.textViewDateReport

        val idPost = arguments?.getLong("post_id") ?: -1
        val posts = postViewModel.posts.value ?: emptyList()
        var postResponse = PostResponse.empty()

        for (post: PostResponse in posts) {
            if (idPost == post.postId) {
                postResponse = post
                break
            }
        }

        val parser = SimpleDateFormat("yyyy-MM-dd")
        val formatter = SimpleDateFormat("dd.MM.yyyy")

        date.text = formatter.format(parser.parse(postResponse.dateChanged)?: "0.0.0") // if data is null return 0.0.0
        nik.text = postResponse.nikName
        load.getIcon(postResponse.userId, icon)
        load.getImage(postResponse.image?.id, image)

        if (!postResponse.hashtags.isNullOrEmpty()) hashtag.text = postResponse.hashtags!!.map { it.hashtagName }.reduce { acc, s -> "$acc $s" }

        buttonReport.setOnClickListener {
            var id = 0

            if (spam.isChecked) {
                id = 0
            } else if (candid.isChecked) {
                id = 1
            } else if (deception.isChecked) {
                id = 2
            } else if (prohibited.isChecked) {
                id = 3
            } else if (violence.isChecked) {
                id = 4
            }

            postViewModel.complaintReportOnPost(idPost, id)
            findNavController().navigate(R.id.action_reportComplaintFragment_to_feedFragment)

        }
    }
}