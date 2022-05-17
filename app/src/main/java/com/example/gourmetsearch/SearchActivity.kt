package com.example.gourmetsearch

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SearchActivity : AppCompatActivity(), LocationListener {

    val service = RetrofitClient.getService()

    //検索条件の変数
    var latitude = 0.0 // 緯度
    var longitude = 0.0 // 軽度
    var radius = 100 // 検索範囲

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchView = findViewById<SearchView>(R.id.search_view)

        //GPSを起動
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //権限の確認
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "位置情報が許可されていません。", Toast.LENGTH_SHORT)
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50.0f, this)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                requestRestaurantData(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun requestRestaurantData(keyword: String) {
        service.getRestaurantData(getString(R.string.api_key), keyword, latitude, longitude, radius)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.let {
                        TODO("JSONファイルとして取得する。配列に代入する。")
                        Log.d("TAG","${it.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "お店が見つかりませんでした。", Toast.LENGTH_SHORT)
                }
            })
    }

    override fun onLocationChanged(location: Location) {
        //緯度軽度を更新
        latitude = location.latitude
        longitude = location.longitude
    }
}