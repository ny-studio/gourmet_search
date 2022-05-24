package com.example.gourmetsearch

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.net.URL
import javax.xml.xpath.XPathFactory

class MyRecyclerViewAdapter(val restaurantsData: List<RestaurantData>) :
    RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //XPathを解釈するオブジェクトを生成
        val xPath = XPathFactory.newInstance().newXPath()

        val shop = restaurantsData[position].restaurant
        val name = restaurantsData[position].name
        val logo = restaurantsData[position].logoImage
        val genre = restaurantsData[position].genre
        val access = restaurantsData[position].access
        val address = xPath.evaluate("./address/text()", shop)
        val openTime = xPath.evaluate("./open/text()", shop)
        val contentText = xPath.evaluate("./photo/mobile/l/text()",shop)
        val catch = xPath.evaluate("./genre/catch/text()",shop)
        val urls = xPath.evaluate("./urls/pc/text()",shop)

        holder.logoImage.setImageBitmap(logo)
        holder.nameText.text = name
        holder.accessText.text = access
        holder.genreText.text = genre
        holder.progressBar.visibility = View.INVISIBLE        //進捗バーを非表示

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("logo", logo)
            intent.putExtra("address", address)
            intent.putExtra("access",access)
            intent.putExtra("genre",genre)
            intent.putExtra("open_time",openTime)
            intent.putExtra("content",contentText)
            intent.putExtra("catch",catch)
            intent.putExtra("weblink",urls)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = restaurantsData.size
}