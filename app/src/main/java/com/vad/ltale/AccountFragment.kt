package com.vad.ltale

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.data.Record
import java.io.File
import java.io.FilenameFilter
import java.net.URI
import java.util.*


class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonCreateRecord: FloatingActionButton = view.findViewById(R.id.createRecordButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemRecords)

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
        val dir = File(path)
        val files = dir.listFiles(FilenameFilter {_, name -> name.endsWith(".mp3")})

        println(files.size)

        val listRecord = mutableListOf<Record>()

        for (recordFiles in files!!) {
            listRecord.add(Record(1, recordFiles.name, URI(recordFiles.absolutePath), recordFiles.absolutePath, Date(124), 122))
        }

//        val listRecord = listOf(
//            Record(10, "hello", URI(""), Date(123424424), 1000),
//            Record(10, "hello1", URI(""), Date(123424424), 1000),
//            Record(10, "hello2", URI(""), Date(123424424), 1000),
//            Record(10, "hello3", URI(""), Date(123424424), 1000)
//            )

        val adapter = RecordAdapter()
        adapter.setRecords(listRecord)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
        buttonCreateRecord.setOnClickListener { view.findNavController().navigate(R.id.action_accountFragment_to_recordFragment) }
    }
}