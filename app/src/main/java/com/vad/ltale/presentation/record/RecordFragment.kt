package com.vad.ltale.presentation.record

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.FileResponse
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.ChunkTimer
import com.vad.ltale.domain.RecordAudioHandle
import com.vad.ltale.domain.Supplier
import com.vad.ltale.domain.TimerHandler
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.RecordAdapter
import java.io.File

class RecordFragment : Fragment(), OnTouchListener, TimerHandler {

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer
    private lateinit var actionButton: FloatingActionButton
    private lateinit var buttonSave: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordAdapter

    private lateinit var contextThis: Context
    private lateinit var recorder: RecordAudioHandle
    private lateinit var mainViewModel: MainViewModel
    private lateinit var postViewModel: PostViewModel
    private var audio: File? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextThis = context;
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                contextThis,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                contextThis,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                contextThis,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        } else {
            val factory = PostViewModelFactory(PostRepository(mainViewModel.getRetrofit()))
            postViewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

            chunkTimer = ChunkTimer(1000 * 60)
            recorder = RecordAudioHandle(chunkTimer)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chunkTimer.setTimerHandler(this)
        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        actionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
        recyclerView = view.findViewById(R.id.audioRecyclerRecord)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecordAdapter()

        buttonSave = view.findViewById(R.id.saveButton) as Button
        buttonSave.isActivated = false

        buttonSave.setOnClickListener {
            if (audio != null) {
                postViewModel.savePost(audio ?: File(""), null, mainViewModel.getUserDetails().userId)
                findNavController().popBackStack()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        val listAudio: MutableList<FileResponse> = arrayListOf()

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> recorder.startRecording()
            MotionEvent.ACTION_UP -> {
                buttonSave.isActivated = true
                audio = recorder.stopRecording()
                listAudio.add(FileResponse(audio?.name ?: ""))
                adapter.setRecords(listAudio)
                recyclerView.adapter = adapter
            }
            MotionEvent.ACTION_CANCEL -> {
                buttonSave.isActivated = true
                audio = recorder.stopRecording()
                listAudio.add(FileResponse(audio?.name ?: ""))
                adapter.setRecords(listAudio)
                recyclerView.adapter = adapter
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun showTime(time: Long) {
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        timeRecordTextView.text = "$minutes:$seconds"
    }

    override fun finishTime() {
        timeRecordTextView.text = "end"
    }

}