package com.example.weatherapp

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShortForecastAdapter(private val dataSet : ArrayList<ShortForecastItem>) : RecyclerView.Adapter<ShortForecastAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val timeView : TextView
        init{
            timeView = view.findViewById(R.id.timeText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}