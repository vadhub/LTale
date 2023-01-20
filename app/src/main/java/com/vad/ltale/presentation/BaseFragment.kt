package com.vad.ltale.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import com.vad.ltale.domain.Supplier

open class BaseFragment : Fragment() {
    protected lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

}