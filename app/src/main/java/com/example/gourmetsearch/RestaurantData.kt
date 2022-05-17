package com.example.gourmetsearch

import android.media.Image

data class RestaurantData(
    val id:String,
    val name:String,
    val address:String,
    val logoImage:Image
)
