package com.vad.ltale

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.vad.ltale.presentation.MainViewModel
import com.vad.ltale.presentation.account.Supplier


class MainActivity : AppCompatActivity(), Supplier<MainViewModel> {

    private lateinit var navController: NavController
    private lateinit var saveDataPref: SaveDataPref
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveDataPref = SaveDataPref(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        mainViewModel.setUserId(2)
        println(mainViewModel.getUserId())

        navController.navigate(R.id.accountFragment)

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

    override fun get(): MainViewModel = mainViewModel

}