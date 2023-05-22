package com.vad.ltale.presentation.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.data.AudioRequest
import com.vad.ltale.data.Limit
import com.vad.ltale.data.PlayView
import com.vad.ltale.data.repository.LimitRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.domain.*
import com.vad.ltale.domain.audiohandle.PlayHandler
import com.vad.ltale.domain.audiohandle.Player
import com.vad.ltale.domain.audiohandle.Recorder
import com.vad.ltale.domain.timehandle.ChunkTimer
import com.vad.ltale.domain.timehandle.TimerHandler
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.AudioAdapter
import com.vad.ltale.presentation.adapter.PlayOnClickListener
import java.io.File
import java.sql.Date
import java.sql.Timestamp
import java.util.concurrent.TimeUnit

class RecordFragment : BaseFragment(), OnTouchListener, TimerHandler, PlayOnClickListener {

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer
    private lateinit var actionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AudioAdapter
    private var selectedImage: Intent? = null
    private lateinit var playHandler: PlayHandler
    private lateinit var listAudio: List<Audio>
    private lateinit var recorder: Recorder
    private lateinit var postViewModel: PostViewModel
    private val listAudioRequest: MutableList<AudioRequest> = ArrayList()
    private lateinit var limitViewModel: LimitViewModel
    private lateinit var limit: Limit
    private var time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                thisContext,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                thisContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                thisContext,
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

            val limitFactory = LimitViewModelFactory(LimitRepository(mainViewModel.getRetrofit()))
            limitViewModel = ViewModelProvider(this, limitFactory).get(LimitViewModel::class.java)

            limitViewModel.getLimit(mainViewModel.getUserDetails().userId)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val image: ImageView = view.findViewById(R.id.imageViewPostRecord)
        val imageButton: ImageButton = view.findViewById(R.id.imageButtonChoose)

        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        actionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
        recyclerView = view.findViewById(R.id.audioRecyclerRecord)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AudioAdapter(this)

        limitViewModel.limit.observe(viewLifecycleOwner) {
            limit = it
            chunkTimer = ChunkTimer(it.time)
            recorder = Recorder(chunkTimer)
            chunkTimer.setTimerHandler(this)
        }

        playHandler = PlayHandler(Player(thisContext))


        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                selectedImage = it.data!!
                image.setImageURI(selectedImage?.data)
            }
        }

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_navigation, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            if (listAudioRequest.isNotEmpty()) {
                var file: File? = null
                Log.d("##bustsave", "save")
                if (selectedImage != null) {
                    file = File(FileUtil.getPath(selectedImage?.data, context))
                }
                postViewModel.savePost(listAudioRequest, file, mainViewModel.getUserDetails().userId)
                limitViewModel.updateTime(Limit(limit.id, mainViewModel.getUserDetails().userId, time, "${Date(System.currentTimeMillis())}"))
            } else {
                Toast.makeText(thisContext, "Record audio", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigate(R.id.action_to_accountFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> recorder.startRecording()
            MotionEvent.ACTION_UP -> saveAudio()
            MotionEvent.ACTION_CANCEL -> saveAudio()
        }
        return true
    }

    private fun saveAudio() {
        val audio: AudioRequest = recorder.stopRecording()
        Log.d("##record", "play ${audio.file}")
        listAudioRequest.add(audio)
        listAudio = listAudioRequest.map { la -> Audio(
            uri = la.file.absolutePath,
            duration = la.duration,
            date = "${Timestamp(System.currentTimeMillis())}"
        ) }.toMutableList()
        adapter.setRecords(listAudio)
        recyclerView.adapter = adapter
    }

    override fun showTime(time: Long) {
        this.time = time
        val mTime = String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(time),
            TimeUnit.MILLISECONDS.toSeconds(time) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)))
        timeRecordTextView.text = mTime
    }

    override fun finishTime() {
        timeRecordTextView.text = "end"
    }

    override fun onItemClick(playView: PlayView){
        playHandler.handlePlayView(playView)
    }

}