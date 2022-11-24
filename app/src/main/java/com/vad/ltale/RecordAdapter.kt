package com.vad.ltale

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vad.ltale.data.Record

class RecordAdapter : Adapter<RecordAdapter.RecordViewHolder>() {

    private lateinit var records: List<Record>

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(records: List<Record>) {
        this.records = records
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) =
        holder.bind(records.get(position).title, records.get(position).duration)

    override fun getItemCount(): Int = records.size

    class RecordViewHolder(item: View) : ViewHolder(item) {

        private var titleTextView: TextView
        private var timeTextView: TextView

        init {
            titleTextView = item.findViewById(R.id.audioTitleTextView)
            timeTextView = item.findViewById(R.id.audioTimeTextView)

            item.findViewById<ImageButton>(R.id.handleAudioImageButton).setOnClickListener {
                Log.d("----AdapterRecord", "click")
            }
        }

        fun bind(title: String, duration: Long) {
            titleTextView.text = title
            timeTextView.text = "$duration"
        }

    }
}