package com.example.gourmetsearch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.net.URI
import java.net.URL

class MyRecyclerViewAdapter(val restaurantsData: List<RestaurantData>):RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.logoImage.setImageBitmap(restaurantsData[position].logoImage)
        holder.nameText.text = restaurantsData[position].name
        println(restaurantsData[position].logoImage)
    }

    override fun getItemCount(): Int = restaurantsData.size
}