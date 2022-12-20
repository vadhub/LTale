package com.vad.ltale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.vad.ltale.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.create

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        //sendPost()

        runBlocking {
            launch {
                println("---------------------------")
                println(RetrofitInstance().apiUser.getUser().body()?.embedded?.users)
            }
        }

        sendFile(File("/storage/self/primary/Pictures/test.txt"))
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun sendFile(file: File) {

        Log.e("file", file.absolutePath)
        val requestFile: RequestBody = create("multipart/form-data".toMediaTypeOrNull(), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.getName(), requestFile)

        runBlocking {
            launch {
                RetrofitInstance().apiUpload.uploadFile(body)
            }
        }
    }

}