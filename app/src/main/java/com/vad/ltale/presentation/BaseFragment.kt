package com.vad.ltale.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import com.vad.ltale.data.local.SaveConfiguration

open class BaseFragment : Fragment() {
    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var thisContext: Context
    protected lateinit var configuration: SaveConfiguration

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.thisContext = context
        configuration = SaveConfiguration(thisContext)
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

}