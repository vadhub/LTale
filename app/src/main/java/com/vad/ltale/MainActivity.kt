package com.vad.ltale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.vad.ltale.data.remote.RetrofitInstance
import java.io.File
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.Message
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LoadViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
//        sendFile(File("/storage/self/primary/Pictures/test.txt"))

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}