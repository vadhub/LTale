package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.PostResponse
import com.vad.ltale.presentation.FileViewModel
import java.text.SimpleDateFormat


class PostAdapter(
    private val load: FileViewModel,
    private val likeOnClickListener: LikeOnClickListener,
    private val onClickAccount: AccountClickListener,
    private val userId: Long,
    private val player: ExoPlayer
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<PostResponse> = emptyList()

    private var playingPosition = -1
    private var playingParentHolder: PostViewHolder? = null

    fun updateNonPlaying(parentHolder: PostViewHolder?) {
        Log.d("@item 3update", "$!1 $playingParentHolder")
        parentHolder?.adapter?.updateNonPlayingLast()
        parentHolder?.adapter?.resetPlayingPosition()
    }

    fun updateStopPlayingLast() {
        updateNonPlaying(playingParentHolder)
    }

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

    inner class PostViewHolder(itemView: View) : ViewHolder(itemView) {
        private val textViewDate = itemView.findViewById(R.id.textViewDate) as TextView
        private val imageViewPost = itemView.findViewById(R.id.imageViewPost) as ImageView
        private val recyclerViewAudio = itemView.findViewById(R.id.audioRecycler) as RecyclerView
        lateinit var adapter: AudioAdapter
        private val textViewCountLike = itemView.findViewById(R.id.countLikes) as TextView
        val imageViewLike = itemView.findViewById(R.id.like) as ImageButton
        val imageIcon = itemView.findViewById(R.id.imageIconPost) as ShapeableImageView
        private val hashtag = itemView.findViewById(R.id.textViewHashtag) as TextView

        fun bind(postResponse: PostResponse) {

            imageIcon.setImageDrawable(null)
            imageViewPost.setImageDrawable(null)

            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd.MM.yyyy")

            textViewDate.text = formatter.format(parser.parse(postResponse.dateChanged))

            load.getIcon(postResponse.userId, itemView.context, imageIcon)
            load.getImage(postResponse.image?.id, itemView.context, imageViewPost)

            if (postResponse.hashtags.isNotEmpty()) hashtag.text = postResponse.hashtags.map { it.hashtagName }.reduce { acc, s -> "$acc $s" }

            recyclerViewAudio.layoutManager = LinearLayoutManager(itemView.context)
            adapter = AudioAdapter(player)
            adapter.setRecords(postResponse.listAudio)
            recyclerViewAudio.adapter = adapter

            adapter.setOnClick {
                onClick()
            }
        }

        val onClick: () -> Unit = {
            Log.d("itemClick", "$playingPosition $layoutPosition $this $playingParentHolder")
            if (playingPosition != layoutPosition) {
                Log.d("itemClick", "update $")
                updateNonPlaying(playingParentHolder)
                playingParentHolder = this
                playingPosition = layoutPosition
            }
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
