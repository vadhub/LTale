package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
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
import com.vad.ltale.data.Audio
import com.vad.ltale.data.Hashtag
import com.vad.ltale.data.PostResponse
import com.vad.ltale.presentation.FileViewModel

class PostAdapter(
    private val load: FileViewModel,
    private val onClickListener: PlayOnClickListener,
    private val likeOnClickListener: LikeOnClickListener,
    private val onClickAccount: AccountClickListener,
    private val userId: Long
) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private var posts: List<PostResponse> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setPosts(posts: List<PostResponse>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false),
            onClickListener
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val post = posts[position]

        Log.d("", "onBindViewHolder: $position ${post.userId}")

        if (post.countLike > 0) holder.likeHandle(userId == post.userId, post.countLike)

        holder.imageIcon.setOnClickListener {
            onClickAccount.onClick(post.userId)
        }

        holder.imageViewLike.setOnClickListener {
            likeOnClickListener.onLike(post, position)
        }

        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    inner class MyViewHolder(itemView: View, private val onClickListener: PlayOnClickListener) :
        ViewHolder(itemView) {
        private val textViewDate = itemView.findViewById(R.id.textViewDate) as TextView
        private val imageViewPost = itemView.findViewById(R.id.imageViewPost) as ImageView
        private val recyclerViewAudio = itemView.findViewById(R.id.audioRecycler) as RecyclerView
        private val textViewCountLike = itemView.findViewById(R.id.countLikes) as TextView
        val imageViewLike = itemView.findViewById(R.id.like) as ImageButton
        val imageIcon = itemView.findViewById(R.id.imageIconPost) as ShapeableImageView
        private val hashtag = itemView.findViewById(R.id.textViewHashtag) as TextView

        fun bind(postResponse: PostResponse) {

            imageIcon.setImageDrawable(null)
            imageViewPost.setImageDrawable(null)

            textViewDate.text = postResponse.dateChanged

            load.getIcon(postResponse.userId, itemView.context, imageIcon)
            load.getImage(postResponse.image?.id, itemView.context, imageViewPost)

            if (postResponse.hashtags.isNotEmpty()) hashtag.text = postResponse.hashtags.map { it.hashtagName }.reduce { acc, s -> "$acc $s" }

            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = AudioAdapter(onClickListener)
            adapter.setRecords(postResponse.listAudio)
            recyclerViewAudio.adapter = adapter
        }

        fun likeHandle(isLiked: Boolean, countLike: Int) {
            Log.d("##postAdapter", "likeHandle: $isLiked")
            if (isLiked) {
                imageViewLike.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_favorite_24))
            } else {
                imageViewLike.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_outline_favorite_border_24))
            }
            textViewCountLike.text = "$countLike"
        }
    }

}
