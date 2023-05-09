package com.vad.ltale.presentation

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var thisContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.thisContext = context
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

}