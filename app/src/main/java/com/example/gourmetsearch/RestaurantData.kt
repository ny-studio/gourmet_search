package com.example.gourmetsearch

import android.graphics.Bitmap
import android.media.Image
import android.os.AsyncTask
import org.w3c.dom.Node

data class RestaurantData(
    var restaurant: Node,
    var id:String,
    var name:String,
    var address:String,
    var logoImage: Bitmap?,
    var genre:String,
)

