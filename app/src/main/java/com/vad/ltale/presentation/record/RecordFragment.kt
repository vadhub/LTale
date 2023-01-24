package com.vad.ltale.presentation.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.AudioRequest
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.*
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.RecordAdapter
import java.io.File

class RecordFragment : BaseFragment(), OnTouchListener, TimerHandler, RecyclerOnClickListener {

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer
    private lateinit var actionButton: FloatingActionButton
    private lateinit var buttonSave: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordAdapter
    private val player by lazy { PlayAudioHandle() }

    private lateinit var recorder: RecordAudioHandle
    private lateinit var postViewModel: PostViewModel
    private var audio: File? = null
    private val listAudio: MutableList<AudioRequest> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
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

        val image: ImageView = view.findViewById(R.id.imageViewPostRecord)
        val imageButton: ImageButton = view.findViewById(R.id.imageButtonChoose)
        var selectedImage: Intent? = null

        chunkTimer.setTimerHandler(this)
        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        actionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
        recyclerView = view.findViewById(R.id.audioRecyclerRecord)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecordAdapter()

        buttonSave = view.findViewById(R.id.saveButton) as Button
        buttonSave.isActivated = false

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                selectedImage = it.data!!
                image.setImageURI(selectedImage!!.data)
            }
        }

        buttonSave.setOnClickListener {
            if (audio != null) {
                postViewModel.savePost(listAudio, File(FileUtil.getPath(selectedImage!!.data, context)) , mainViewModel.getUserDetails().userId)
                findNavController().popBackStack()
            }
        }

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> recorder.startRecording()
            MotionEvent.ACTION_UP -> saveAudio()
            MotionEvent.ACTION_CANCEL -> saveAudio()
        }
        return true
    }

    private fun saveAudio() {
        buttonSave.isActivated = true
        listAudio.add(recorder.stopRecording())
        adapter.setRecords(listAudio)
        recyclerView.adapter = adapter
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

    override fun onItemClick(position: Int){
        player.initializePlayer(listAudio.get(position).file.absolutePath)
        player.playAudio()
    }

}