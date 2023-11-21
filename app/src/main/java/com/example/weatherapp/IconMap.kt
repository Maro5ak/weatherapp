package com.example.weatherapp

class IconMap {

    companion object{
        private val map = hashMapOf(
            Pair("01d", R.drawable.icon_01d),
            Pair("01n", R.drawable.icon_01n),
            Pair("02d", R.drawable.icon_02d),
            Pair("02n", R.drawable.icon_02n),
            Pair("03d", R.drawable.icon_03d),
            Pair("03n", R.drawable.icon_03n),
            Pair("04d", R.drawable.icon_04d),
            Pair("04n", R.drawable.icon_04n),
            Pair("09d", R.drawable.icon_09d),
            Pair("09n", R.drawable.icon_09n),
            Pair("10d", R.drawable.icon_10d),
            Pair("10n", R.drawable.icon_10n),
            Pair("11d", R.drawable.icon_11d),
            Pair("11n", R.drawable.icon_11n),
            Pair("13d", R.drawable.icon_13d),
            Pair("13n", R.drawable.icon_13n),
            Pair("50d", R.drawable.icon_50d),
            Pair("50n", R.drawable.icon_50n),
        )

        fun get(key: String) : Int{
            return map[key] ?: 0
        }
    }
}