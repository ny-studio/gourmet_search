package com.example.gourmetsearch
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("gourmet/v1/")
    fun getRestaurantData(
        @Query("key")apiKey:String,
        @Query("keyword")keyword:String,
        @Query("lat")latitude:Double,
        @Query("lng")longitude:Double,
        @Query("range")radius:Int,

    ): Call<ResponseBody>
}