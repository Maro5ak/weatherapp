package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.time.Instant
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

interface ResponseCallback{
    fun onShortResponseSuccess(json: String, country: String)
    fun onFullResponseSuccess(json: String, country: String)
}

class MainActivity : AppCompatActivity(), ResponseCallback {
    private val parser = JSONParser()
    private lateinit var recycler : RecyclerView
    private lateinit var longRecycler : RecyclerView
    private lateinit var loadingDialog : AlertDialog

    private var currentIndex = 0
    private var shortDone = false
    private var longDone = false

    private val intentLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            when (result.data?.getIntExtra("activity", -1)) {
                -1 -> throw Exception()
                0 -> {
                    val res = result.data?.getIntExtra("data", 0)
                    currentIndex = res ?: 0
                    apiCall()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item0 -> {
                intentLaunch.launch(Intent(this, WeatherSelect::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

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

    @SuppressLint("SetTextI18n")
    private fun updateCurrentTemps(low : Int, high : Int, temp: Int, weather : String, city: String, country: String, wind: Int, feels: Int, sunrise: String, sunset: String){
        findViewById<TextView>(R.id.cityText).text = city
        findViewById<TextView>(R.id.currentWeatherText).text = weather
        findViewById<TextView>(R.id.currentTempText).text = "$temp°C"
        findViewById<TextView>(R.id.highLowText).text = "H: $high°C | L: $low°C"
        findViewById<TextView>(R.id.windSpeedText).text = wind.toString()
        findViewById<TextView>(R.id.feelsLikeText).text = "$feels°C"
        findViewById<TextView>(R.id.sunriseText).text = sunrise
        findViewById<TextView>(R.id.sunsetText).text = sunset

        val shared = this.getPreferences(MODE_PRIVATE)
        with(shared.edit()){
            putString(currentIndex.toString(), "$city;$country;$temp;$high;$low;")
            commit()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var textColor = ContextCompat.getColor(this, R.color.text_def)
        var cardColor = ContextCompat.getColor(this, R.color.card_def)
        if(LocalTime.now().isBefore(LocalTime.of(SWAP_TIME, 0))){
            findViewById<ConstraintLayout>(R.id.parentLayout).setBackgroundResource(R.drawable.gradient)
        }
        else {
            findViewById<ConstraintLayout>(R.id.parentLayout).setBackgroundResource(R.drawable.gradient_night)
            textColor = ContextCompat.getColor(this, R.color.text_night)
            cardColor = ContextCompat.getColor(this, R.color.card_night)
        }
        setColor(textColor)
        setCardColor(cardColor)
        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val managerLong = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler = findViewById(R.id.forecastShortList)
        longRecycler = findViewById(R.id.forecastLongList)
        recycler.layoutManager = manager
        longRecycler.layoutManager = managerLong


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Loading..")
            .setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog.show()

        apiCall()
    }
    private fun apiCall(){
        shortDone = false
        longDone = false
        val shared = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val csv = shared.getString(currentIndex.toString(), "").toString()
        val csvSplit = csv.splitToSequence(";")
        val city = csvSplit.elementAt(0)
        val country = csvSplit.elementAt(1)

        getFullForecast(city, country, this)
        getCurrentForecast(city, country, this)

    }

    private fun updateShort(currentJson: String, country: String){
        val currentForecast = parser.parseForecastCurrent(currentJson, country)

        updateCurrentTemps(currentForecast.low, currentForecast.high, currentForecast.temp, currentForecast.current, currentForecast.city, currentForecast.country, currentForecast.wind, currentForecast.feelsLike, currentForecast.sunrise, currentForecast.sunset)
    }
    private fun updateLong(longJson: String, country : String){
        val longForecast = parser.parseForecastLong(longJson, country)

        val dataset = ArrayList<ShortForecastItem>()
        val longDataset = ArrayList<LongForecastItem>()
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        var lastDay = 0
        var avgDayTempHigh = 0
        var avgDayTempLow = 0
        var avgDayTempExp = 0
        var count = 0
        for((index, i) in longForecast.withIndex()){
            calendar.time = i.time
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            if(lastDay != day || index == longForecast.size - 1) {
                if(lastDay != 0) {
                    if(count == 0) count = 1
                    longDataset.add(LongForecastItem(if(lastDay == today) "Today" else get(lastDay), IconMap.get(i.icon),
                        avgDayTempLow / count,
                        avgDayTempHigh / count,
                        avgDayTempExp / count))
                }
                lastDay = day
                avgDayTempExp = 0
                avgDayTempHigh = 0
                avgDayTempLow = 0
                count = 0
            }
            else {
                avgDayTempHigh += i.high
                avgDayTempLow += i.low
                avgDayTempExp += i.temp
                count++
            }

            if(!i.time.before(Date.from(Instant.now())) && i.time.before(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))) {
                val time = calendar.get(Calendar.HOUR_OF_DAY)
                dataset.add(ShortForecastItem(time, IconMap.get(i.icon), i.temp))
            }

        }

        val adapter = ShortForecastAdapter(dataset)
        val longAdapter = LongForecastAdapter(longDataset)
        recycler.adapter = adapter
        longRecycler.adapter = longAdapter
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

    override fun onShortResponseSuccess(json: String, country: String) {
        updateShort(json, country)
        shortDone = true
        if(longDone) loadingDialog.cancel()
    }

    override fun onFullResponseSuccess(json: String, country: String) {
        updateLong(json, country)
        longDone = true
        if(shortDone) loadingDialog.cancel()
    }

    private fun setColor(color: Int){
        findViewById<TextView>(R.id.sunriseText).setTextColor(color)
        findViewById<TextView>(R.id.sunsetText).setTextColor(color)
        findViewById<TextView>(R.id.feelsLikeText).setTextColor(color)
        findViewById<TextView>(R.id.windSpeedText).setTextColor(color)
        findViewById<TextView>(R.id.windSpeedUnitText).setTextColor(color)
        findViewById<TextView>(R.id.windText).setTextColor(color)
        findViewById<TextView>(R.id.feelsLikeHeaderText).setTextColor(color)
        findViewById<TextView>(R.id.sunriseHeaderText).setTextColor(color)
        findViewById<TextView>(R.id.sunsetHeaderText).setTextColor(color)
        findViewById<TextView>(R.id.cityText).setTextColor(color)
        findViewById<TextView>(R.id.currentTempText).setTextColor(color)
        findViewById<TextView>(R.id.highLowText).setTextColor(color)
        findViewById<TextView>(R.id.currentWeatherText).setTextColor(color)
        findViewById<TextView>(R.id.sunriseTimeNote).setTextColor(color)
        findViewById<TextView>(R.id.sunsetTimeNote).setTextColor(color)
    }
    private fun setCardColor(color: Int){
        findViewById<CardView>(R.id.shortListCard).setCardBackgroundColor(color)
        findViewById<CardView>(R.id.longListCard).setCardBackgroundColor(color)
        findViewById<CardView>(R.id.moreDataCard).setCardBackgroundColor(color)
    }
}