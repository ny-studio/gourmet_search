package com.example.gourmetsearch

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
    val logoImage:ImageView = itemView.findViewById(R.id.item_image)
    val nameText:TextView = itemView.findViewById(R.id.item_title)
}