package com.example.weatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CitySelectAdapter(private val dataSet: ArrayList<CitySelectItem>) : RecyclerView.Adapter<CitySelectAdapter.ViewHolder>() {
    var onItemClick: ((CitySelectItem) -> Unit)? = null
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val cityView : TextView
        val tempView : TextView
        val countryView: TextView
        init{
            cityView = view.findViewById(R.id.cityListText)
            tempView = view.findViewById(R.id.tempText)
            countryView = view.findViewById(R.id.countryListText)

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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityView.text = dataSet[position].name
        holder.tempView.text = "${dataSet[position].temp}°C"
        holder.countryView.text = dataSet[position].country.uppercase()
    }
}