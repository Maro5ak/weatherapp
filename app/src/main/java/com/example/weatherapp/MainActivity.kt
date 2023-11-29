package com.example.weatherapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView

interface ResponseCallback{
    fun onShortResponseSuccess(json: String, country: String)
    fun onFullResponseSuccess(json: String, country: String)
}

class MainActivity : AppCompatActivity(), ResponseCallback {
    private var currentIndex = 0

    private var shortJson : String? = null
    private var longJson : String? = null
    private lateinit var navBar : BottomNavigationView
    private lateinit var navBarLand : NavigationRailView

    private fun getCurrentForecast(cityName: String, country: String, callback: ResponseCallback){

        val req = "https://api.openweathermap.org/data/2.5/weather?q=$cityName,$country&appid=${BuildConfig.api_key}&units=metric"

        val q = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, req, null,
            { response ->
                val res = response.toString()
                callback.onShortResponseSuccess(res, country)
            },
            { error ->
                Log.wtf("Err", error.toString())
            }
        )
        q.add(jsonObjectRequest)

    }

    private fun getFullForecast(cityName: String, country: String, callback : ResponseCallback){
        val req = "https://api.openweathermap.org/data/2.5/forecast?q=$cityName,$country&appid=${BuildConfig.api_key}&units=metric"

        val q = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, req, null,
            { response ->
                val res = response.toString()
                callback.onFullResponseSuccess(res, country)
            },
            { error ->
                Log.wtf("Err", error.toString())
            }
        )
        q.add(jsonObjectRequest)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navBar = findViewById(R.id.navBar)
        navBarLand = findViewById(R.id.navBarLand)

        navBar.setOnItemSelectedListener {
            when (it.itemId){
                R.id.bar0 -> {
                    apiCall()
                    true
                }
                R.id.bar1 -> {
                    fragmentLoad(WeatherSelect())
                    true
                }
                R.id.bar2 -> {
                    fragmentLoad(CityAddFragment())
                    true
                }
                else -> false
            }

        }

        navBarLand.setOnItemSelectedListener {
            when (it.itemId){
                R.id.bar0 -> {
                    apiCall()
                    true
                }
                R.id.bar1 -> {
                    fragmentLoad(WeatherSelect())
                    true
                }
                R.id.bar2 -> {
                    fragmentLoad(CityAddFragment())
                    true
                }
                else -> false
            }

        }
        val shared = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val countCheck = shared.getInt("count", -1)
        if(countCheck == -1){
            with(shared.edit()){
                putInt("count", 0)
                commit()
            }
        }

        apiCall()
    }


    fun apiCall(index : Int? = null){
        try {
            longJson = null
            shortJson = null
            val shared = this.getPreferences(Context.MODE_PRIVATE) ?: return
            if (index != null)
                currentIndex = index
            val csv = shared.getString(currentIndex.toString(), "").toString()
            val csvSplit = csv.splitToSequence(";")
            val city = csvSplit.elementAt(0)
            val country = csvSplit.elementAt(1)

            getFullForecast(city, country, this)
            getCurrentForecast(city, country, this)
        }
        catch(e: Exception){
            Log.e("Error", "No weather data found!")
        }
    }

    companion object{
        const val SWAP_TIME = 16
        private val map = hashMapOf(
            Pair(1, "Sun"),
            Pair(2, "Mon"),
            Pair(3, "Tue"),
            Pair(4, "Wed"),
            Pair(5, "Thu"),
            Pair(6, "Fri"),
            Pair(7, "Sat")
        )

        fun get(index: Int) : String{
            return map[index] ?: "None"
        }
    }

    private fun fragmentLoad(fragment : Fragment, args : Bundle? = null){
        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.currentFragment, fragment).commit()
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            supportFragmentManager.beginTransaction().replace(R.id.landscapeFragment, WeatherSelect()).commit()
    }

    override fun onShortResponseSuccess(json: String, country: String) {
        //updateShort(json, country)
        shortJson = json
        if(longJson != null) fragmentLoad(CurrentForecastFragment(),
            bundleOf("short" to shortJson, "long" to longJson, "country" to country, "index" to currentIndex))
    }

    override fun onFullResponseSuccess(json: String, country: String) {
        //updateLong(json, country)
        longJson = json
        if(shortJson != null) fragmentLoad(CurrentForecastFragment(),
            bundleOf("short" to shortJson, "long" to longJson, "country" to country, "index" to currentIndex))
    }
}