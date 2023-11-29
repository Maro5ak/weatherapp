package com.example.weatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime

class CityAddAdapter(private val dataSet: ArrayList<CityAddItem>) : RecyclerView.Adapter<CityAddAdapter.ViewHolder>() {
    var onItemClick: ((CityAddItem) -> Unit)? = null
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val cityView : TextView
        val countryView: TextView
        val background : ConstraintLayout
        init{
            cityView = view.findViewById(R.id.cityAddName)
            countryView = view.findViewById(R.id.cityAddCountry)
            background = view.findViewById(R.id.cityAddRoot)
            view.setOnClickListener{
                onItemClick?.invoke(dataSet[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.city_add, parent, false)
        )
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityView.text = dataSet[position].cityName
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
        holder.countryView.setTextColor(color)

    }
}