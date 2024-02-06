package com.vad.ltale.presentation.record

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.exception.GetTimeException
import com.vad.ltale.data.remote.exception.UpdateException
import com.vad.ltale.data.repository.LimitRepository
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.databinding.FragmentRecordBinding
import com.vad.ltale.model.audiohandle.Recorder
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.AudioRequest
import com.vad.ltale.model.pojo.Limit
import com.vad.ltale.model.timehandle.ChunkTimer
import com.vad.ltale.model.timehandle.TimeFormatter
import com.vad.ltale.model.timehandle.TimerHandler
import com.vad.ltale.presentation.*
import com.vad.ltale.presentation.adapter.AudioAdapter
import com.vad.ltale.presentation.animation.AnimationButton
import java.io.File
import java.sql.Timestamp

class RecordFragment : AudioBaseFragment(), OnTouchListener, TimerHandler, View.OnClickListener {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

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

    private lateinit var chunkTimer: ChunkTimer
    private var adapter: AudioAdapter? = null
    private lateinit var listAudio: MutableList<Audio>
    private var recorder: Recorder? = null
    private val listAudioRequest: MutableList<AudioRequest> = ArrayList()
    private lateinit var limit: Limit
    private var time: Long = 0
    private val hashtags: MutableList<String> = mutableListOf()
    private val chips: MutableList<Chip> = mutableListOf()
    private var animationButton: AnimationButton? = null
    private val userId = RemoteInstance.user.userId
    private var file: File? = null

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
                if (s[s.length - 1].code == 32 || s[s.length - 1].code == 10) {
                    //32 is ascii code for space, do something when condition is true.
                    createTag(binding.editTextHashtag.text.trim())
                    binding.editTextHashtag.setText("")
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

        permissionLauncherMultiple.launch(permissions.toTypedArray())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val image: ImageView = binding.imageViewPostRecord
        val imageButton: ImageButton = view.findViewById(R.id.imageButtonChoose)
        val progressBarOnActionButton: ProgressBar = view.findViewById(R.id.progressBarActionButton)
        val removeImage: ImageButton = view.findViewById(R.id.removeImage)

        binding.recordFloatingButton.setOnTouchListener(this)
        binding.audioRecyclerRecord.layoutManager = LinearLayoutManager(context)
        binding.editTextHashtag.addTextChangedListener(hashtagTextWatcher)

        val removeAudioListener: (audio: Audio) -> Unit = {
            removeAudio(it)
        }

        adapter = AudioAdapter(0, prepareAudioHandleWithoutViewModel(), true)
        adapter?.removeListener = removeAudioListener

        binding.audioRecyclerRecord.adapter = adapter

        limitViewModel.limit.observe(viewLifecycleOwner) {
            limit = it

            if (limit.time < 1000) {
                binding.timeLastTextView.text = getString(R.string.time_is_up)
                binding.textViewMessageForRecord.text = getString(R.string.come_back_tomorrow)
                binding.recordFloatingButton.setImageResource(R.drawable.mic_off_fill0_wght200_grad0_opsz24)
                binding.recordFloatingButton.isEnabled = false
                binding.editTextHashtag.isEnabled = false
                imageButton.visibility = GONE
            } else {
                binding.timeLastTextView.text = TimeFormatter.format(it.time)
                chunkTimer = ChunkTimer(it.time)
                recorder = Recorder(chunkTimer, thisContext)
                chunkTimer.setTimerHandler(this)
                animationButton = AnimationButton(recorder!!, OvershootInterpolator())
                imageButton.visibility = VISIBLE
            }

            progressBarOnActionButton.visibility = GONE
            binding.recordFloatingButton.visibility = VISIBLE
            binding.timeLastTextView.visibility = VISIBLE
        }

        removeImage.setOnClickListener {
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

        imageButton.setOnClickListener {
            startCrop()
        }

    }

    private fun createTag(text: CharSequence) {
        var str = text

        if (str.isNotBlank()) {

            if (str[0] != '#') {
                str = "#$str"
            }

            createChip(str, binding.chipGroup)
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

                chips.forEach { hashtags.add(it.text.toString()) }
                postViewModel.savePost(
                    thisContext,
                    listAudioRequest,
                    file,
                    userId,
                    hashtags.ifEmpty { null })
                limitViewModel.time = time

            } else {
                Toast.makeText(thisContext, getString(R.string.record_audio), Toast.LENGTH_SHORT).show()
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
                animationButton?.startCaptureAudioVolume(binding.recordFloatingButton)
                recorder?.startRecording()
                true
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                animationButton?.stopCapture()
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
        adapter?.setRecords(listAudio)
        binding.textViewMessageForRecord.visibility = GONE
    }

    private fun removeAudio(audio: Audio) {
        listAudio.remove(audio)
        listAudioRequest.removeAll { uri -> uri.file.absolutePath == audio.uri }
        adapter?.setRecords(listAudio)
        time = (time / 1000) * 1000 //needed for leveling
        time += audio.duration
        binding.timeLastTextView.text = TimeFormatter.format(time)
        chunkTimer.setTimeStartFrom(time)

        if (listAudio.isEmpty()) {
            binding.textViewMessageForRecord.visibility = VISIBLE
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.getUriFilePath(thisContext, true)

            uriContent?.let {
                binding.imageViewPostRecord.setImageURI(Uri.parse(uriContent))
                file = File(uriContent)
            }
        } else {
            Toast.makeText(thisContext, getString(R.string.can_t_load_image), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun startCrop() {
        cropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeGallery = true,
                    imageSourceIncludeCamera = true
                ),
            ),
        )
    }

    override fun showTime(time: Long) {
        this.time = time
        binding.timeLastTextView.text = TimeFormatter.format(time)
    }

    override fun finishTime() {
        binding.recordFloatingButton.setImageResource(R.drawable.mic_off_fill0_wght200_grad0_opsz24)
        binding.recordFloatingButton.isEnabled = false
        binding.timeLastTextView.text = getString(R.string.time_is_up)
        Toast.makeText(thisContext, getString(R.string.time_is_up), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        binding.chipGroup.removeView(v)
        chips.remove(v)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        player.stop()
        adapter = null
    }

}
