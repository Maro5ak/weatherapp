package com.example.weatherapp

import org.json.JSONObject
import java.security.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.util.Date

class JSONParser {

    fun parseForecastCurrent(json: String?, country: String, city : String? = null) : Forecast{
        if(json == null) return Forecast("none", "none", "none", 0, 0, 0, "none", Date.from(Instant.now()))
        val jsonObject = JSONObject(json)

        val localCity = city ?: jsonObject.getString("name")
        val weatherArr = jsonObject.getJSONArray("weather").getJSONObject(0)
        val currentWeather = weatherArr.getString("main")
        val icon = weatherArr.getString("icon")
        val temp = jsonObject.getJSONObject("main").getDouble("temp").toInt()
        val high = jsonObject.getJSONObject("main").getDouble("temp_max").toInt()
        val low = jsonObject.getJSONObject("main").getDouble("temp_min").toInt()
        val timestamp = jsonObject.getLong("dt")

        return Forecast(localCity, country, currentWeather, low, high, temp, icon, Date.from(Instant.ofEpochSecond(timestamp)))
    }

    fun parseForecastLong(json: String?, country: String) : ArrayList<Forecast>{
        val res = ArrayList<Forecast>()
        if(json == null) return res
        val parentJson = JSONObject(json)
        val jsonArray = parentJson.getJSONArray("list")
        val city = parentJson.getJSONObject("city").getString("name")
        for(i in 0 until jsonArray.length()){
            res.add(parseForecastCurrent(jsonArray[i].toString(), country, city))
        }
        return res
    }
}