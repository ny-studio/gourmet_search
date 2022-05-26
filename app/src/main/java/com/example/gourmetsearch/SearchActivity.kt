package com.example.gourmetsearch

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import org.w3c.dom.NodeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class SearchActivity : AppCompatActivity(), LocationListener {
    val service = RetrofitClient.getService()

    //検索条件の変数
    var latitude: Double = 0.0 // 緯度
    var longitude: Double = 0.0 // 経度

    //リスト表示するデータ
    var restaurantsData: ArrayList<RestaurantData> = arrayListOf<RestaurantData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //アクションバーを非表示
        supportActionBar?.hide()

        //検索ビュー
        val searchView = findViewById<SearchView>(R.id.search_view)

        //オプションボタン
        val optionButton = findViewById<ImageButton>(R.id.option_button)

        //main.xmlを読み込む
        val dropDownMenu = PopupMenu(applicationContext, optionButton)
        val menu = dropDownMenu.menu
        dropDownMenu.menuInflater.inflate(R.menu.main, menu)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                requestRestaurantData(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                restaurantsData.clear()
                updateRecyclerView()
                return false
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                restaurantsData.clear()
                updateRecyclerView()
                return false
            }
        })

        optionButton.setOnClickListener {
            dropDownMenu.show()
        }

        dropDownMenu.setOnMenuItemClickListener {
            if (it.itemId == R.id.option) {
                val dialog = OptionDialogFragment()
                dialog.show(supportFragmentManager, "option")
            }else if(it.itemId == R.id.credit){
                val dialog = CreditDialogFragment()
                dialog.show(supportFragmentManager,"credit")
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        //権限の確認
        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                val locationManager: LocationManager =
                    getSystemService(LOCATION_SERVICE) as LocationManager
                //GPSを起動
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)

                //最後に確認された位置情報を取得
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location.latitude    //緯度
                    longitude = location.longitude  //経度
                }
            }
            else -> {
                //警告メッセージを表示
                Toast.makeText(applicationContext, "位置情報が許可されていません。", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //権限許可をリクエストする
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        //緯度経度を更新
        latitude = location.latitude
        longitude = location.longitude
    }

    fun requestRestaurantData(keyword: String) {
        val sharedPref = getSharedPreferences("search_params", Context.MODE_PRIVATE)
        val range = sharedPref.getInt("range", 3)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        service.getRestaurantData(
            getString(R.string.api_key),
            keyword,
            latitude,
            longitude,
            range,
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.let {
                        //xmlから店情報を取得する
                        restaurantsData = parseXML(it.string())
                        val adapter = MyRecyclerViewAdapter(restaurantsData)
                        recyclerView.adapter = adapter

                        for (r in restaurantsData) {
                            downloadImage(r).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR,
                                r.logoImageText
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "お店が見つかりませんでした。", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    fun parseXML(xml: String): ArrayList<RestaurantData> {
        //xmlを解析してDOM Documentに変換するビルダー
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        //xmlを解析してDOM Documentを生成
        val stream = ByteArrayInputStream(xml.toByteArray())
        val document = builder.parse(stream)
        stream.close()
        //XPathを解釈するオブジェクトを生成
        val xPath = XPathFactory.newInstance().newXPath()

        //shopノードをすべて取得
        val shops = xPath.evaluate("//shop", document, XPathConstants.NODESET) as NodeList
        if (shops.length == 0) {
            Toast.makeText(
                applicationContext,
                "お店が見つかりませんでした。\nキーワード・条件を変えてみましょう。",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val available = xPath.evaluate("./results/results_available/text()", document)
            Toast.makeText(
                applicationContext,
                "${available}件のお店が見つかりました。",
                Toast.LENGTH_SHORT
            ).show()
        }

        val restaurantsData: ArrayList<RestaurantData> = arrayListOf<RestaurantData>()

        for (i in 0 until shops.length) {
            val shop = shops.item(i)
            val name = xPath.evaluate("./name/text()", shop)
            val logo = xPath.evaluate("./logo_image/text()", shop)
            val access = xPath.evaluate("./mobile_access/text()", shop)
            val genre = xPath.evaluate("./genre/name/text()", shop)

            val restaurantData = RestaurantData(shop, "0", name, access, logo, null, genre)
            restaurantsData.add(restaurantData)
        }

        return restaurantsData
    }

    private inner class downloadImage(var restaurantData: RestaurantData) :
        AsyncTask<String, Int, Bitmap>() {
        override fun doInBackground(vararg params: String?): Bitmap {
            val url = URL(params[0])
            val input = url.openStream()
            val image = BitmapFactory.decodeStream(input)
            return image
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            restaurantData.logoImage = bitmap
            //画像がインストールされたあとリサイクラービューを更新
            updateRecyclerView()
        }
    }

    fun updateRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}