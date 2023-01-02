package com.vad.ltale.presentation.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.remote.RetrofitInstance
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LoadViewModelFactory
import com.vad.ltale.presentation.MainViewModel


class AccountFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)
        val imageIcon: ImageView = view.findViewById(R.id.imageIcon)
        //val adapter = RecordAdapter()
        //adapter.setRecords(listRecord)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        //recyclerView.adapter = adapter

        val factory = LoadViewModelFactory(RetrofitInstance())
        val load: FileViewModel = ViewModelProvider(this, factory).get(FileViewModel::class.java)

        val id:Int = mainViewModel.getUserId()
        println("$id ---------------------")

        //load.uploadFile(File("/storage/self/primary/Pictures/test.txt"), Message("Hello world!", "", id))
//        load.fileResponseBody.observe(viewLifecycleOwner) {
//
//            imageIcon.setImageBitmap(BitmapFactory.decodeStream(it.byteStream()))
//        }
//        load.downloadFile("gaugehg.png", "audio", "$id")

        buttonCreateRecord.setOnClickListener { view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment) }
    }
}