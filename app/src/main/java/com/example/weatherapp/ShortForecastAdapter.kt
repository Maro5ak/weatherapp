package com.example.weatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime

class ShortForecastAdapter(private val dataSet : ArrayList<ShortForecastItem>) : RecyclerView.Adapter<ShortForecastAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val timeView : TextView
        val imageView: ImageView
        val tempView: TextView
        init{
            timeView = view.findViewById(R.id.timeText)
            imageView = view.findViewById(R.id.forecastShortIcon)
            tempView = view.findViewById(R.id.temperatureText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        )
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = dataSet[position].time
        holder.timeView.text = "$time:00"
        holder.imageView.setImageResource(dataSet[position].icon)
        holder.tempView.text = "${dataSet[position].temperature}°C"
        var color = ContextCompat.getColor(holder.itemView.context, R.color.text_def)
        if(LocalTime.now().isAfter(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            color = ContextCompat.getColor(holder.itemView.context, R.color.text_night)
        }
        holder.timeView.setTextColor(color)
        holder.tempView.setTextColor(color)
    }
}