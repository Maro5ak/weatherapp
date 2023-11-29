package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date


class CurrentForecastFragment : Fragment(R.layout.fragment_current_forecast) {

    private var shortForecastStr: String? = null
    private var longForecastStr: String? = null
    private var country: String? = null

    private val parser = JSONParser()
    private lateinit var recycler : RecyclerView
    private lateinit var longRecycler : RecyclerView

    private var currentIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var textColor = ContextCompat.getColor(view.context, R.color.text_def)
        var cardColor = ContextCompat.getColor(view.context, R.color.card_def)
        if(LocalTime.now().isBefore(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            view.findViewById<ConstraintLayout>(R.id.parentLayout).setBackgroundResource(R.drawable.gradient)
        }
        else {
            view.findViewById<ConstraintLayout>(R.id.parentLayout).setBackgroundResource(R.drawable.gradient_night)
            textColor = ContextCompat.getColor(view.context, R.color.text_night)
            cardColor = ContextCompat.getColor(view.context, R.color.card_night)
        }

        setColor(textColor)
        setCardColor(cardColor)
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val managerLong = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler = view.findViewById(R.id.forecastShortList)
        longRecycler = view.findViewById(R.id.forecastLongList)
        recycler.layoutManager = manager
        longRecycler.layoutManager = managerLong

        updateShort(shortForecastStr ?: return)
        updateLong(longForecastStr ?: return)

    }
    @SuppressLint("SetTextI18n")
    private fun updateCurrentTemps(low : Int, high : Int, temp: Int, weather : String, city: String, country: String, wind: Int, feels: Int, sunrise: String, sunset: String){
        val view = view ?: return
        view.findViewById<TextView>(R.id.cityText).text = city
        view.findViewById<TextView>(R.id.currentWeatherText).text = weather
        view.findViewById<TextView>(R.id.currentTempText).text = "$temp°C"
        view.findViewById<TextView>(R.id.highLowText).text = "H: $high°C | L: $low°C"
        view.findViewById<TextView>(R.id.windSpeedText).text = wind.toString()
        view.findViewById<TextView>(R.id.feelsLikeText).text = "$feels°C"
        view.findViewById<TextView>(R.id.sunriseText).text = sunrise
        view.findViewById<TextView>(R.id.sunsetText).text = sunset

        val shared = context?.getSharedPreferences("MainActivity", AppCompatActivity.MODE_PRIVATE) ?: return
        with(shared.edit()){
            putString(currentIndex.toString(), "$city;$country;$temp;$high;$low;false;")
            commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        shortForecastStr = requireArguments().getString("short")
        longForecastStr = requireArguments().getString("long")
        country = requireArguments().getString("country")
        currentIndex = requireArguments().getInt("index")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_forecast, container, false)
    }

    private fun updateShort(currentJson: String){
        val currentForecast = parser.parseForecastCurrent(currentJson, country ?: "none")

        updateCurrentTemps(currentForecast.low, currentForecast.high, currentForecast.temp, currentForecast.current, currentForecast.city, currentForecast.country, currentForecast.wind, currentForecast.feelsLike, currentForecast.sunrise, currentForecast.sunset)
    }
    private fun updateLong(longJson: String){
        val longForecast = parser.parseForecastLong(longJson, country ?: "none")

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
                    longDataset.add(LongForecastItem(if(lastDay == today) "Today" else MainActivity.get(lastDay), IconMap.get(i.icon),
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
    private fun setColor(color: Int){
        val view = view ?: return
        view.findViewById<TextView>(R.id.sunriseText).setTextColor(color)
        view.findViewById<TextView>(R.id.sunsetText).setTextColor(color)
        view.findViewById<TextView>(R.id.feelsLikeText).setTextColor(color)
        view.findViewById<TextView>(R.id.windSpeedText).setTextColor(color)
        view.findViewById<TextView>(R.id.windSpeedUnitText).setTextColor(color)
        view.findViewById<TextView>(R.id.windText).setTextColor(color)
        view.findViewById<TextView>(R.id.feelsLikeHeaderText).setTextColor(color)
        view.findViewById<TextView>(R.id.sunriseHeaderText).setTextColor(color)
        view.findViewById<TextView>(R.id.sunsetHeaderText).setTextColor(color)
        view.findViewById<TextView>(R.id.cityText).setTextColor(color)
        view.findViewById<TextView>(R.id.currentTempText).setTextColor(color)
        view.findViewById<TextView>(R.id.highLowText).setTextColor(color)
        view.findViewById<TextView>(R.id.currentWeatherText).setTextColor(color)
        view.findViewById<TextView>(R.id.sunriseTimeNote).setTextColor(color)
        view.findViewById<TextView>(R.id.sunsetTimeNote).setTextColor(color)
    }
    private fun setCardColor(color: Int){
        val view = view ?: return
        view.findViewById<CardView>(R.id.shortListCard).setCardBackgroundColor(color)
        view.findViewById<CardView>(R.id.longListCard).setCardBackgroundColor(color)
        view.findViewById<CardView>(R.id.moreDataCard).setCardBackgroundColor(color)
    }
}