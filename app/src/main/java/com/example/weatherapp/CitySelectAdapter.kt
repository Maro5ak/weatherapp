package com.example.weatherapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.xmlpull.v1.XmlPullParser
import java.sql.Date
import java.time.Instant
import java.time.LocalTime
import java.util.Calendar

class CitySelectAdapter(private val dataSet: ArrayList<CitySelectItem>) : RecyclerView.Adapter<CitySelectAdapter.ViewHolder>() {
    var onItemClick: ((CitySelectItem) -> Unit)? = null
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val cityView : TextView
        val tempView : TextView
        val countryView: TextView
        val background : ConstraintLayout
        init{
            cityView = view.findViewById(R.id.cityListText)
            tempView = view.findViewById(R.id.tempText)
            countryView = view.findViewById(R.id.countryListText)
            background = view.findViewById(R.id.rootLayout)
            view.setOnClickListener{
                onItemClick?.invoke(dataSet[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.city_row, parent, false)
        )
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityView.text = dataSet[position].name
        holder.tempView.text = "${dataSet[position].temp}°C"
        holder.countryView.text = dataSet[position].country.uppercase()
        var color = ContextCompat.getColor(holder.itemView.context, R.color.text_def)
        if(LocalTime.now().isBefore(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            holder.background.setBackgroundResource(R.drawable.gradient)
        }
        else{
            holder.background.setBackgroundResource(R.drawable.gradient_night)
            color = ContextCompat.getColor(holder.itemView.context, R.color.text_night)
        }
        holder.cityView.setTextColor(color)
        holder.tempView.setTextColor(color)
        holder.countryView.setTextColor(color)

    }
}