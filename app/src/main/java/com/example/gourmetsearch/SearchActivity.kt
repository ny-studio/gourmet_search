package com.example.gourmetsearch

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.ResponseBody
import org.w3c.dom.NodeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class SearchActivity : AppCompatActivity(), LocationListener {

    val service = RetrofitClient.getService()

    //検索条件の変数
    var latitude:Double = 0.0 // 緯度
    var longitude:Double = 0.0 // 軽度
    var radius = 5 // 検索範囲

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchView = findViewById<SearchView>(R.id.search_view)
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

    override fun onResume() {
        super.onResume()
        //権限の確認
        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                //GPSを起動
                val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)

                //最後に確認された位置情報を取得
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(location != null){
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
            else -> {
                Toast.makeText(applicationContext, "位置情報が許可されていません。", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //権限許可をリクエストする
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        //緯度軽度を更新
        latitude = location.latitude
        longitude = location.longitude
    }

    fun requestRestaurantData(keyword: String) {
        service.getRestaurantData(getString(R.string.api_key), keyword, latitude, longitude, radius)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    var xml = ""
                    response.body()?.let {
                        xml = it.string()
                        println(response)
                    }
                    //xmlを解析してDOM Documentに変換するビルダー
                    val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    //xmlを解析してDOM Documentを生成
                    val stream = ByteArrayInputStream(xml.toByteArray())
                    val document = builder.parse(stream)
                    stream.close()
                    //XPathを解釈するオブジェクトを生成
                    val xPath = XPathFactory.newInstance().newXPath()

                    //shopノードをすべて取得
                    val shops = xPath.evaluate("//shop",document, XPathConstants.NODESET) as NodeList
                    for(i in 0 until shops.length) {
                        val shop = shops.item(i)
                        val name = xPath.evaluate("./name/text()",shop)
                        val logo = xPath.evaluate("./logo_image/text()",shop)
                        val address = xPath.evaluate("./address/text()",shop)
                        println(name)
                        println(logo)
                        println(address)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "お店が見つかりませんでした。", Toast.LENGTH_SHORT).show()
                }
            })
    }
}