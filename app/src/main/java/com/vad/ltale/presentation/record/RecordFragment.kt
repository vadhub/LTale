package com.vad.ltale.presentation.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.repository.LimitRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.Audio
import com.vad.ltale.model.AudioRequest
import com.vad.ltale.model.FileUtil
import com.vad.ltale.model.Limit
import com.vad.ltale.model.TimeFormatter
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.model.audiohandle.Recorder
import com.vad.ltale.model.timehandle.ChunkTimer
import com.vad.ltale.model.timehandle.TimerHandler
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.AudioAdapter
import java.io.File
import java.sql.Date
import java.sql.Timestamp

class RecordFragment : BaseFragment(), OnTouchListener, TimerHandler, View.OnClickListener {

    private val postViewModel: PostViewModel by activityViewModels { PostViewModelFactory(PostRepository(mainViewModel.getRetrofit())) }
    private val limitViewModel: LimitViewModel by activityViewModels { LimitViewModelFactory(LimitRepository(mainViewModel.getRetrofit())) }

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer
    private lateinit var actionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AudioAdapter
    private var selectedImage: Intent? = null
    private lateinit var listAudio: MutableList<Audio>
    private lateinit var recorder: Recorder
    private val listAudioRequest: MutableList<AudioRequest> = ArrayList()
    private lateinit var limit: Limit
    private var time: Long = 0
    private val hashtags: MutableList<String> = mutableListOf()
    private lateinit var hashtag: EditText
    private lateinit var chipGroup: ChipGroup
    private val chips: MutableList<Chip> = mutableListOf()

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
        val player: ExoPlayer = ExoPlayer.Builder(thisContext).build()
        chipGroup = view.findViewById(R.id.chipGroup)

        hashtag = view.findViewById(R.id.editTextHashtag)

        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        actionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
        recyclerView = view.findViewById(R.id.audioRecyclerRecord)
        recyclerView.layoutManager = LinearLayoutManager(context)

        hashtag.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event != null) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE && event.action == MotionEvent.ACTION_UP) {
                        createTag(hashtag.text.trim())
                        hashtag.setText("")
                        return true
                    }
                }
                return false
            }

        })


        val play: (audio: Audio, changePlayItem: () -> Unit) -> Unit =
            { audio: Audio, changePlayItem: () -> Unit ->
                player.setMediaItem(MediaItem.fromUri(audio.uri))
                player.prepare()
                player.play()
                changePlayItem.invoke()
            }

        val playlistHandler = PlaylistHandler(player, play)

        val removeAudioListener: (audio: Audio) -> Unit = {
            removeAudio(it)
        }

        adapter = AudioAdapter(0, playlistHandler, true)

        adapter.removeListener = removeAudioListener

        limitViewModel.limit.observe(viewLifecycleOwner) {
            limit = it
            chunkTimer = ChunkTimer(it.time)
            recorder = Recorder(chunkTimer, thisContext)
            chunkTimer.setTimerHandler(this)
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                selectedImage = it.data!!
                image.layoutParams.height = 150
                image.setImageURI(selectedImage?.data)
            }
        }

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

    }

    private fun createTag(text: CharSequence) {
        var str = text

        if (str.isNotBlank()) {

            if (str[0] != '#') {
                str = "#$str"
            }

            createChip(str, chipGroup)
        }
    }

    private fun createChip(str: CharSequence, chipGroup: ChipGroup) {
        val chip = Chip(thisContext)
        chip.text = str
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener(this)
        if (chips.size <= 4) {
            chipGroup.addView(chip)
            chips.add(chip)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_navigation, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            if (listAudioRequest.isNotEmpty()) {
                var file: File? = null
                if (selectedImage != null) {
                    file = File(FileUtil.getPath(selectedImage?.data, context))
                }

                chips.forEach { hashtags.add(it.text.toString()) }

                postViewModel.savePost(listAudioRequest, file, mainViewModel.getUserDetails().userId,
                    hashtags.ifEmpty { null })
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
        listAudioRequest.add(audio)
        listAudio = listAudioRequest.map { la -> Audio(
            uri = la.file.absolutePath,
            duration = la.duration,
            date = "${Timestamp(System.currentTimeMillis())}"
        ) }.toMutableList()
        adapter.setRecords(listAudio)
        recyclerView.adapter = adapter
    }

    private fun removeAudio(audio: Audio) {
        listAudio.remove(audio)
        listAudioRequest.removeAll {uri -> uri.file.absolutePath == audio.uri }
        adapter.setRecords(listAudio)
        time += audio.duration
        timeRecordTextView.text = TimeFormatter.format(time)
        chunkTimer.setTimeStartFrom(time)
    }

    override fun showTime(time: Long) {
        this.time = time
        timeRecordTextView.text = TimeFormatter.format(time)
    }

    override fun finishTime() {
        timeRecordTextView.text = "end"
    }

    override fun onClick(v: View?) {
        chipGroup.removeView(v)
        chips.remove(v)
    }

}