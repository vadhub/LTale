package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import com.vad.ltale.R
import com.vad.ltale.data.FileResponse
import com.vad.ltale.data.Post
import com.vad.ltale.presentation.FileViewModel

class PostAdapter(private val load: FileViewModel) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private var posts: List<Post> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setPosts(posts: List<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        load.getImage(posts.get(position).idImage, holder.itemView.context)?.into(holder.imageViewPost)

        holder.bind(
            posts.get(position).date,
            posts.get(position).audio
        )
    }

    override fun getItemCount() = posts.size

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        private val textViewDate = itemView.findViewById(R.id.textViewDate) as TextView
        val imageViewPost = itemView.findViewById(R.id.imageViewPost) as ImageView
        private val recyclerViewAudio = itemView.findViewById(R.id.audioRecycler) as RecyclerView

        fun bind(date: String, audios: List<FileResponse>) {
            textViewDate.text = date
            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = RecordAdapter()
            adapter.setRecords(audios)
            recyclerViewAudio.adapter = adapter
        }
    }

}
