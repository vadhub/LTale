package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.databinding.ItemPostBinding
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.presentation.FileViewModel
import java.text.SimpleDateFormat

class PostAdapter(
    private val load: FileViewModel,
    private val likeOnClickListener: LikeOnClickListener,
    private val onClickAccount: AccountClickListener,
    private val onReachEndListener: () -> Unit,
    private val playlistHandler: PlaylistHandler
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<PostResponse> = emptyList()

    // if is null so that adapter no under account fragment
    var moreClickListener: ((idPost: Long, View) -> Unit)? = null
    var reportClickListener: ((idPost: Long) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setPosts(posts: List<PostResponse>) {
        if (posts.isNotEmpty()) {
            this.posts = posts
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        val post = posts[position]

        if (position == posts.size - 1) {
            onReachEndListener.invoke()
        }

        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(itemView: ItemPostBinding) : ViewHolder(itemView.root), View.OnClickListener {
        private val textViewDate = itemView.textViewDate
        private val imageViewPost = itemView.imageViewPost
        private val recyclerViewAudio = itemView.audioRecycler
        private lateinit var adapter: AudioAdapter
        private val textViewCountLike = itemView.countLikes
        private val imageViewLike = itemView.like
        private val imageIcon = itemView.imageIconPost
        private val hashtag = itemView.textViewHashtag
        private val nikName = itemView.textViewNikName
        private var postResponse = PostResponse.empty()
        private var moreOptions = itemView.moreOptions
        private var reportButton = itemView.complaintReport

        @SuppressLint("SimpleDateFormat")
        fun bind(postResponse: PostResponse) {
            this.postResponse = postResponse

            // if is null so that adapter no under account fragment
            if (moreClickListener != null) {
                moreOptions.visibility = View.VISIBLE
                moreOptions.setOnClickListener {
                    moreClickListener?.invoke(postResponse.postId, moreOptions)
                }
            }

            if (reportClickListener != null) {
                reportButton.visibility = if (RemoteInstance.user.userId != postResponse.userId) View.VISIBLE else View.GONE
                reportButton.setOnClickListener {
                    reportClickListener?.invoke(postResponse.postId)
                }
            }

            imageIcon.setImageDrawable(null)
            imageViewPost.setImageDrawable(null)
            hashtag.text = ""

            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd.MM.yyyy")

            textViewDate.text = formatter.format(parser.parse(postResponse.dateChanged)?: "0.0.0") // if data is null return 0.0.0
            nikName.text = postResponse.nikName

            if (postResponse.image != null) {
                load.getImage(postResponse.image.id, imageViewPost)
            } else {
                imageViewPost.setImageDrawable(null)
            }

            load.getIcon(postResponse.userId, imageIcon)

            if (!postResponse.hashtags.isNullOrEmpty()) hashtag.text = postResponse.hashtags.map { it.hashtagName }.reduce { acc, s -> "$acc $s" }

            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            adapter = AudioAdapter(layoutPosition, playlistHandler, false)
            adapter.setRecords(postResponse.listAudio)
            recyclerViewAudio.adapter = adapter

            imageIcon.setOnClickListener(this)
            imageViewLike.setOnClickListener(this)

            likeHandle(postResponse.isLiked, postResponse.countLike)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun likeHandle(isLiked: Boolean, countLike: Int) {
            if (isLiked) {
                imageViewLike.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_favorite_24))
            } else {
                imageViewLike.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_outline_favorite_border_24))
            }
            textViewCountLike.text = "$countLike"
        }

        override fun onClick(v: View?) {
            when (v) {
                imageIcon -> {onClickAccount.onClick(postResponse.userId)}
                imageViewLike -> {likeOnClickListener.onLike(postResponse, layoutPosition)}
            }
        }
    }

}
