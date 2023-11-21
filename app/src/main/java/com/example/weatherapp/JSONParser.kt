package com.example.weatherapp

import org.json.JSONObject
import java.security.Timestamp
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

class JSONParser {
    private fun convertDigits(input : Int) : String {
        return if (input < 10) "0$input" else "$input"
    }

    fun parseForecastCurrent(json: String?, country: String) : CurrentForecast{
        if(json == null)
            return CurrentForecast("none", "none", "none", 0, 0, 0, "none", Date.from(Instant.now()), 0, 0, "12:00", "12:00")
        val jsonObject = JSONObject(json)
        val city = jsonObject.getString("name")
        val weatherArr = jsonObject.getJSONArray("weather").getJSONObject(0)
        val currentWeather = weatherArr.getString("main")
        val icon = weatherArr.getString("icon")
        val temp = jsonObject.getJSONObject("main").getDouble("temp").toInt()
        val high = jsonObject.getJSONObject("main").getDouble("temp_max").toInt()
        val low = jsonObject.getJSONObject("main").getDouble("temp_min").toInt()
        val feels = jsonObject.getJSONObject("main").getDouble("feels_like").toInt()
        val wind = jsonObject.getJSONObject("wind").getDouble("speed").toInt()
        val sunriseTimestamp = jsonObject.getJSONObject("sys").getLong("sunrise")
        val sunriseDt = Instant.ofEpochSecond(sunriseTimestamp).atZone(ZoneId.systemDefault()).toLocalTime()
        val sunsetTimestamp = jsonObject.getJSONObject("sys").getLong("sunset")
        val sunsetDt = Instant.ofEpochSecond(sunsetTimestamp).atZone(ZoneId.systemDefault()).toLocalTime()
        val sunrise = "${convertDigits(sunriseDt.hour)}:${convertDigits(sunriseDt.minute)}"
        val sunset = "${convertDigits(sunsetDt.hour)}:${convertDigits(sunsetDt.minute)}"
        val timestamp = jsonObject.getLong("dt")

        return CurrentForecast(city, country, currentWeather, low, high, temp, icon, Date.from(Instant.ofEpochSecond(timestamp)), feels, wind, sunrise, sunset)
    }
    private fun parseForecast(json: String?, country: String, city : String) : Forecast{
        if(json == null) return Forecast("none", "none", "none", 0, 0, 0, "none", Date.from(Instant.now()))
        val jsonObject = JSONObject(json)
        val weatherArr = jsonObject.getJSONArray("weather").getJSONObject(0)
        val currentWeather = weatherArr.getString("main")
        val icon = weatherArr.getString("icon")
        val temp = jsonObject.getJSONObject("main").getDouble("temp").toInt()
        val high = jsonObject.getJSONObject("main").getDouble("temp_max").toInt()
        val low = jsonObject.getJSONObject("main").getDouble("temp_min").toInt()
        val timestamp = jsonObject.getLong("dt")

        return Forecast(city, country, currentWeather, low, high, temp, icon, Date.from(Instant.ofEpochSecond(timestamp)))
    }

    fun parseForecastLong(json: String?, country: String) : ArrayList<Forecast>{
        val res = ArrayList<Forecast>()
        if(json == null) return res
        val parentJson = JSONObject(json)
        val jsonArray = parentJson.getJSONArray("list")
        val city = parentJson.getJSONObject("city").getString("name")
        for(i in 0 until jsonArray.length()){
            res.add(parseForecast(jsonArray[i].toString(), country, city))
        }
        return res
    }
}