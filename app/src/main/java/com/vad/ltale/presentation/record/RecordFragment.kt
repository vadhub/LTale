package com.vad.ltale.presentation.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.exception.GetTimeException
import com.vad.ltale.data.remote.exception.UpdateException
import com.vad.ltale.data.repository.LimitRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.FileUtil
import com.vad.ltale.model.TimeFormatter
import com.vad.ltale.model.audiohandle.Recorder
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.AudioRequest
import com.vad.ltale.model.pojo.Limit
import com.vad.ltale.model.timehandle.ChunkTimer
import com.vad.ltale.model.timehandle.TimerHandler
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.AudioAdapter
import java.io.File
import java.sql.Date
import java.sql.Timestamp

class RecordFragment : AudioBaseFragment(), OnTouchListener, TimerHandler, View.OnClickListener {

    private val postViewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(
            PostRepository(RemoteInstance)
        )
    }
    private val limitViewModel: LimitViewModel by activityViewModels {
        LimitViewModelFactory(
            LimitRepository(RemoteInstance)
        )
    }

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer
    private lateinit var actionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AudioAdapter
    private var selectedImage: Intent? = null
    private lateinit var listAudio: MutableList<Audio>
    private var recorder: Recorder? = null
    private val listAudioRequest: MutableList<AudioRequest> = ArrayList()
    private lateinit var limit: Limit
    private var time: Long = 0
    private val hashtags: MutableList<String> = mutableListOf()
    private lateinit var hashtag: EditText
    private lateinit var chipGroup: ChipGroup
    private val chips: MutableList<Chip> = mutableListOf()
    private lateinit var textViewRecordToVoice: TextView

    private val permissionLauncherMultiple = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        var areAllGranted = true

        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }

        if (areAllGranted) {
            limitViewModel.getLimit(RemoteInstance.user.userId)
        } else {
            Toast.makeText(
                thisContext,
                getString(R.string.permission_denied_record),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val hashtagTextWatcher: TextWatcher = object : TextWatcher {
        private var lastLength = 0
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            lastLength = s.length
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (lastLength > s.length) return
                if (s[s.length - 1].code == 32) {
                    //32 is ascii code for space, do something when condition is true.
                    createTag(hashtag.text.trim())
                    hashtag.setText("")
                }
            } catch (ex: IndexOutOfBoundsException) {
                //handle the exception
            }
        }

        override fun afterTextChanged(editable: Editable) {
            //do something
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        permissionLauncherMultiple.launch(permissions.toTypedArray())
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
        val progressBarOnActionButton: ProgressBar = view.findViewById(R.id.progressBarActionButton)
        val removeImage: ImageButton = view.findViewById(R.id.removeImage)
        textViewRecordToVoice = view.findViewById(R.id.textView)
        chipGroup = view.findViewById(R.id.chipGroup)
        hashtag = view.findViewById(R.id.editTextHashtag)
        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        actionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
        recyclerView = view.findViewById(R.id.audioRecyclerRecord)
        recyclerView.layoutManager = LinearLayoutManager(context)

        hashtag.addTextChangedListener(hashtagTextWatcher)

        val removeAudioListener: (audio: Audio) -> Unit = {
            removeAudio(it)
        }

        adapter = AudioAdapter(0, prepareAudioHandleWithoutViewModel(), true)
        adapter.removeListener = removeAudioListener

        recyclerView.adapter = adapter

        limitViewModel.limit.observe(viewLifecycleOwner) {
            limit = it
            timeRecordTextView.text = TimeFormatter.format(it.time)
            chunkTimer = ChunkTimer(it.time)
            recorder = Recorder(chunkTimer, thisContext)
            chunkTimer.setTimerHandler(this)

            progressBarOnActionButton.visibility = GONE

            actionButton.visibility = VISIBLE
            imageButton.visibility = VISIBLE
            timeRecordTextView.visibility = VISIBLE
        }

        removeImage.setOnClickListener {
            selectedImage = null
            image.setImageURI(null)
            removeImage.visibility = GONE
        }

        limitViewModel.faller.observe(viewLifecycleOwner) {
            if (it is UpdateException) {
                Toast.makeText(thisContext, getString(R.string.no_update_time), Toast.LENGTH_SHORT)
                    .show()
            } else if (it is GetTimeException) {
                Toast.makeText(
                    thisContext,
                    getString(R.string.get_time_impossible),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    removeImage.visibility = VISIBLE
                    selectedImage = it.data!!
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

                val idUser = RemoteInstance.user.userId

                chips.forEach { hashtags.add(it.text.toString()) }
                postViewModel.savePost(listAudioRequest, file, idUser, hashtags.ifEmpty { null })
                limitViewModel.updateTime(
                    Limit(
                        limit.id,
                        idUser,
                        time,
                        "${Date(System.currentTimeMillis())}"
                    )
                )

            } else {
                Toast.makeText(thisContext, getString(R.string.record_audio), Toast.LENGTH_SHORT)
                    .show()
            }
            findNavController().navigate(R.id.action_to_accountFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                recorder?.startRecording()
                true
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                saveAudio()
                true
            }

            else -> false
        }
    }

    private fun saveAudio() {
        val audio: AudioRequest = recorder!!.stopRecording()
        listAudioRequest.add(audio)
        listAudio = listAudioRequest.map { la ->
            Audio(
                uri = la.file.absolutePath,
                duration = la.duration,
                date = "${Timestamp(System.currentTimeMillis())}"
            )
        }.toMutableList()
        adapter.setRecords(listAudio)
        textViewRecordToVoice.visibility = GONE
    }

    private fun removeAudio(audio: Audio) {
        listAudio.remove(audio)
        listAudioRequest.removeAll { uri -> uri.file.absolutePath == audio.uri }
        adapter.setRecords(listAudio)
        time = (time / 1000) * 1000
        time += audio.duration
        timeRecordTextView.text = TimeFormatter.format(time)
        chunkTimer.setTimeStartFrom(time)

        if (listAudio.isEmpty()) {
            textViewRecordToVoice.visibility = VISIBLE
        }
    }

    override fun showTime(time: Long) {
        this.time = time
        timeRecordTextView.text = TimeFormatter.format(time)
    }

    override fun finishTime() {
        timeRecordTextView.text = "00:00"
        Toast.makeText(thisContext, getString(R.string.time_is_up), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        chipGroup.removeView(v)
        chips.remove(v)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
        player.release()
    }

}