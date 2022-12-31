package com.vad.ltale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.vad.ltale.data.remote.RetrofitInstance
import java.io.File
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.navigateUp
import com.vad.ltale.data.Message
import com.vad.ltale.domain.SaveDataPref
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LoadViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var saveDataPref: SaveDataPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveDataPref = SaveDataPref(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bundle = Bundle()
        bundle.putInt("id", 2)
        navController.navigate(R.id.accountFragment, bundle)

//        if (saveDataPref.getId() == -1) {
//            setupActionBarWithNavController(navController)
//        } else {
//            val bundle = Bundle()
//            bundle.putInt("id", saveDataPref.getId())
//            navController.navigate(R.id.accountFragment, bundle)
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}