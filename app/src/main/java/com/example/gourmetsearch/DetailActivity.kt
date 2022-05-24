package com.example.gourmetsearch

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URI
import java.net.URL

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val nameTextView = findViewById<TextView>(R.id.item_name)
        val logoImageView = findViewById<ImageView>(R.id.item_logo_image)
        val addressTextView = findViewById<TextView>(R.id.item_address)
        val accessTextView = findViewById<TextView>(R.id.item_access)
        val genreTextView = findViewById<TextView>(R.id.item_genre)
        val openTimeTextView = findViewById<TextView>(R.id.item_open_time)
        val catchTextView = findViewById<TextView>(R.id.item_catch)
        val weblinkButton = findViewById<Button>(R.id.item_web_link)

        val name = intent.getStringExtra("name")
        val logo = intent.getParcelableExtra<Bitmap>("logo")
        val address = intent.getStringExtra("address")
        val access = intent.getStringExtra("access")
        val genre = intent.getStringExtra("genre")
        val openTime = intent.getStringExtra("open_time")
        val content = intent.getStringExtra("content")
        val catch = intent.getStringExtra("catch")
        val weblink = intent.getStringExtra("weblink")

        downloadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, content)

        supportActionBar?.title = name
        nameTextView.text = name
        logoImageView.setImageBitmap(logo)
        addressTextView.text = address
        accessTextView.text = access
        genreTextView.text = genre
        openTimeTextView.text = openTime
        catchTextView.text = catch

        weblinkButton.setOnClickListener {
            val uri = Uri.parse(weblink)
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
    }

    private inner class downloadImage() :
        AsyncTask<String, Int, Bitmap>() {
        override fun doInBackground(vararg params: String?): Bitmap {
            val url = URL(params[0])
            val input = url.openStream()
            val image = BitmapFactory.decodeStream(input)
            return image
        }

        override fun onPostExecute(bitmap: Bitmap) {
            val contentImageView = findViewById<ImageView>(R.id.item_content_image)
            contentImageView.setImageBitmap(bitmap)
        }
    }
}