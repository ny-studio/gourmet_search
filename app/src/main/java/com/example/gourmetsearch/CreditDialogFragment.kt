package com.example.gourmetsearch

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class CreditDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログを読み込む
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.credit_dialog)
        return dialog
    }
}