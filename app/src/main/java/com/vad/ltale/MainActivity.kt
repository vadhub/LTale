package com.vad.ltale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.vad.ltale.domain.SaveDataPref
import com.vad.ltale.presentation.MainViewModel
import com.vad.ltale.domain.Supplier


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

        if (saveDataPref.getId() == -1) {
            println(saveDataPref.getId())
            setupActionBarWithNavController(navController)
        } else {
            navController.navigate(R.id.accountFragment)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun get(): MainViewModel = mainViewModel

}