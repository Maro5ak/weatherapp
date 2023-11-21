package com.example.weatherapp

import java.util.Date

class CurrentForecast
    (val city: String, val country: String, val current: String, val low: Int, val high: Int,
     val temp: Int, val icon : String, val time : Date, val feelsLike : Int, val wind : Int,
     val sunrise: String, val sunset: String)  {
}