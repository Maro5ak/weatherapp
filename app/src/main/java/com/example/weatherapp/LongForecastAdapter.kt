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

class LongForecastAdapter(private val dataSet: ArrayList<LongForecastItem>) : RecyclerView.Adapter<LongForecastAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val dayView : TextView
        val imageView: ImageView
        val summaryView: TextView
        init{
            dayView = view.findViewById(R.id.dayView)
            imageView = view.findViewById(R.id.longIcon)
            summaryView = view.findViewById(R.id.summaryText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.long_row_item, parent, false)
        )
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayView.text = dataSet[position].day
        holder.imageView.setImageResource(dataSet[position].icon)
        holder.summaryView.text = "L: ${dataSet[position].low}°C | A: ${dataSet[position].expected}°C | H: ${dataSet[position].high}°C"
        var color = ContextCompat.getColor(holder.itemView.context, R.color.text_def)
        if(LocalTime.now().isAfter(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            color = ContextCompat.getColor(holder.itemView.context, R.color.text_night)
        }
        holder.dayView.setTextColor(color)
        holder.summaryView.setTextColor(color)
    }
}