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
import com.vad.ltale.data.PostResponse
import com.vad.ltale.presentation.FileViewModel

class PostAdapter(
    private val load: FileViewModel,
    private val onClickListener: PlayOnClickListener,
    private val likeOnClickListener: LikeOnClickListener,
    private val onClickAccount: AccountClickListener
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

        val image = posts.get(position).image

        Log.d("", "onBindViewHolder: $position ${posts[position].userId}")
        holder.likeHandle(posts[position].isLiked, posts[position].countLike)

        load.getIcon(posts[position].userId, holder.itemView.context, holder.imageIcon)

        holder.imageIcon.setOnClickListener {
            onClickAccount.onClick(posts[position].userId)
        }

        if (image != null) {
            load.getImage(image.id, holder.itemView.context, holder.imageViewPost)
        }

        holder.imageViewLike.setOnClickListener {
            likeOnClickListener.onLike(posts[position], position)
        }

        holder.bind(
            posts[position].dateCreated,
            posts[position].listAudio
        )
    }

    override fun getItemCount() = posts.size

    class MyViewHolder(itemView: View, private val onClickListener: PlayOnClickListener) :
        ViewHolder(itemView) {
        private val textViewDate = itemView.findViewById(R.id.textViewDate) as TextView
        val imageViewPost = itemView.findViewById(R.id.imageViewPost) as ImageView
        private val recyclerViewAudio = itemView.findViewById(R.id.audioRecycler) as RecyclerView
        private val textViewCountLike = itemView.findViewById(R.id.countLikes) as TextView
        val imageViewLike = itemView.findViewById(R.id.like) as ImageButton
        val imageIcon = itemView.findViewById(R.id.imageIconPost) as ShapeableImageView

        fun bind(date: String, audios: List<Audio>) {
            textViewDate.text = date
            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = AudioAdapter(onClickListener)
            adapter.setRecords(audios)
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
