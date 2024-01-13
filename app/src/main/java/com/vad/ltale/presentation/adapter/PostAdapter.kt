package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.PostResponse
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.presentation.FileViewModel
import java.text.SimpleDateFormat

class PostAdapter(
    private val load: FileViewModel,
    private val likeOnClickListener: LikeOnClickListener,
    private val onClickAccount: AccountClickListener,
    private val userId: Long,
    private val playlistHandler: PlaylistHandler
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<PostResponse> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setPosts(posts: List<PostResponse>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        val post = posts[position]

        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(itemView: View) : ViewHolder(itemView) {
        private val textViewDate = itemView.findViewById(R.id.textViewDate) as TextView
        private val imageViewPost = itemView.findViewById(R.id.imageViewPost) as ImageView
        private val recyclerViewAudio = itemView.findViewById(R.id.audioRecycler) as RecyclerView
        private lateinit var adapter: AudioAdapter
        private val textViewCountLike = itemView.findViewById(R.id.countLikes) as TextView
        private val imageViewLike = itemView.findViewById(R.id.like) as ImageButton
        private val imageIcon = itemView.findViewById(R.id.imageIconPost) as ShapeableImageView
        private val hashtag = itemView.findViewById(R.id.textViewHashtag) as TextView
        private val nikName = itemView.findViewById(R.id.textViewNikName) as TextView

        @SuppressLint("SimpleDateFormat")
        fun bind(postResponse: PostResponse) {

            imageIcon.setImageDrawable(null)
            imageViewPost.setImageDrawable(null)
            nikName.text = ""
            hashtag.text = ""
            textViewDate.text = ""

            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd.MM.yyyy")

            textViewDate.text = formatter.format(parser.parse(postResponse.dateChanged))

            nikName.text = postResponse.nikName

            load.getIcon(postResponse.userId, itemView.context, imageIcon)
            load.getImage(postResponse.image?.id, itemView.context, imageViewPost)

            if (postResponse.hashtags.isNotEmpty()) hashtag.text = postResponse.hashtags.map { it.hashtagName }.reduce { acc, s -> "$acc $s" }

            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            adapter = AudioAdapter(layoutPosition, playlistHandler, false)
            adapter.setRecords(postResponse.listAudio)
            recyclerViewAudio.adapter = adapter

            imageIcon.setOnClickListener {
                onClickAccount.onClick(postResponse.userId)
            }

            imageViewLike.setOnClickListener {
                likeOnClickListener.onLike(postResponse, layoutPosition)
            }

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
    }

}
