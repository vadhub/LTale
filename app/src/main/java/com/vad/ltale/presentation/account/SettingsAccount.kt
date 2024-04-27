package com.vad.ltale.presentation.account

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.vad.ltale.R

class SettingsAccount(private val changeNick: (newNik: String) -> Unit) : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.settings_dialog, null)
        val newNik: EditText = view.findViewById(R.id.newNikEditText)
        val buttonChange: Button = view.findViewById(R.id.changeButton)

        buttonChange.setOnClickListener {
            changeNick.invoke(newNik.text.toString())
            dismiss();
        }

        val builderDialog = AlertDialog.Builder(view.context);
        builderDialog.setView(view)
        return builderDialog.create()
    }
}