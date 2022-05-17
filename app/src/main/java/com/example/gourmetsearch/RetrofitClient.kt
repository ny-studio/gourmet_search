package com.example.gourmetsearch

import retrofit2.Retrofit

object RetrofitClient {
    private var service : ApiService

    init {
        //Retrofitのインスタンスを生成
        val retrofit = Retrofit.Builder().apply {
            baseUrl("https://webservice.recruit.co.jp/hotpepper/")
        }.build()

        service = retrofit.create(ApiService::class.java)
    }

    fun getService():ApiService{
        return service
    }
}