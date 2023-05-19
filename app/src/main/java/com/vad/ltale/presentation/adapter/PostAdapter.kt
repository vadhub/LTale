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
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.data.PostResponse
import com.vad.ltale.presentation.FileViewModel

class PostAdapter(private val load: FileViewModel, private val onClickListener: PlayOnClickListener, private val likeOnClickListener: LikeOnClickListener) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private var posts: List<PostResponse> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setPosts(posts: List<PostResponse>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false), onClickListener)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val image = posts.get(position).image
        
        if (image != null) {
            load.getImage(image.id, holder.itemView.context, holder.imageViewPost)
        }

        holder.imageViewLike.setOnClickListener {
            likeOnClickListener.onLike(posts[position].postId)
        }

        holder.bind(
            posts[position].dateCreated,
            posts[position].listAudio,
            posts[position].countLike,
            posts[position].isLiked
        )

    }

    override fun getItemCount() = posts.size

    class MyViewHolder(itemView: View, private val onClickListener: PlayOnClickListener) : ViewHolder(itemView) {
        private val textViewDate = itemView.findViewById(R.id.textViewDate) as TextView
        val imageViewPost = itemView.findViewById(R.id.imageViewPost) as ImageView
        private val recyclerViewAudio = itemView.findViewById(R.id.audioRecycler) as RecyclerView
        private val textViewCountLike = itemView.findViewById(R.id.countLikes) as TextView
        val imageViewLike = itemView.findViewById(R.id.like) as ImageButton

        fun bind(date: String, audios: List<Audio>, countLike: Int, isLiked: Boolean) {
            textViewDate.text = date
            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = AudioAdapter(onClickListener)
            adapter.setRecords(audios)
            recyclerViewAudio.adapter = adapter
            textViewCountLike.text = "$countLike"

            if (isLiked) {
                imageViewLike.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_favorite_24))
            } else {
                imageViewLike.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_outline_favorite_border_24))
            }

        }
    }

}
