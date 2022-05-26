package com.example.gourmetsearch

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment

class OptionDialogFragment : DialogFragment() {
    @SuppressLint("ResourceType")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログを読み込む
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.option_diglog)

        val sharedPref = activity?.getSharedPreferences("search_params", Context.MODE_PRIVATE)

        //スピナー
        val rangeSpinner = dialog.findViewById<Spinner>(R.id.item_range_spinner)
        val genreSpinner = dialog.findViewById<Spinner>(R.id.item_genre_spinner)

        //スピナーに初期値をセット
        sharedPref?.let { rangeSpinner.setSelection(it.getInt("range",3) - 1) }
        sharedPref?.let { genreSpinner.setSelection(it.getInt("genre",0))}

        //適用ボタン
        dialog.findViewById<Button>(R.id.positive_button).setOnClickListener{
            val range = rangeSpinner.selectedItemPosition + 1
            sharedPref!!.edit().putInt("range",range).apply()

            val genre = genreSpinner.selectedItemPosition
            sharedPref!!.edit().putInt("genre",genre).apply()
            dialog.cancel()
        }

        //キャンセルボタン
        dialog.findViewById<Button>(R.id.negative_button).setOnClickListener{
            dialog.cancel()
        }

        return dialog
    }
}