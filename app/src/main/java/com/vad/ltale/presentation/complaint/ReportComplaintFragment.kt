package com.vad.ltale.presentation.complaint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.RadioButton
import com.vad.ltale.R
import com.vad.ltale.databinding.FragmentRecordBinding
import com.vad.ltale.databinding.FragmentReportComplaintBinding

class ReportComplaintFragment : Fragment() {

    private var _binding: FragmentReportComplaintBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonReport = binding.report

        val spam = binding.spam
        val candid = binding.candid
        val deception = binding.deception
        val prohibited = binding.prohibitedGoods
        val violence = binding.violence

        buttonReport.setOnClickListener {
            var id = 0

            if (spam.isChecked) {
                id = 0
            } else if (candid.isChecked) {
                id = 1
            } else if (deception.isChecked) {
                id = 2
            } else if (prohibited.isChecked) {
                id = 3
            } else if (violence.isChecked) {
                id = 4
            }


        }
    }
}