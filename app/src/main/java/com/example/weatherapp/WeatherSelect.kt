package com.example.weatherapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime


class AddCityFragment : DialogFragment(){

    private lateinit var listener: DialogListener
    interface DialogListener{
        fun onDialogPositiveClick(city: String, country: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement DialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it, R.style.TransparentDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.city_add, null)
            val cityInput = view.findViewById<EditText>(R.id.cityAddInput)
            val countryInput = view.findViewById<EditText>(R.id.countryAddInput)
            view.findViewById<Button>(R.id.btnAdd).setOnClickListener{
                if(cityInput.text.isNotEmpty() && countryInput.text.isNotEmpty())
                    listener.onDialogPositiveClick(cityInput.text.toString(), countryInput.text.toString().lowercase())
                else
                    Toast.makeText(context, "Invalid City or Country!", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener{
                dialog?.dismiss()
            }
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}

class WeatherSelect : Fragment(), AddCityFragment.DialogListener {

    private lateinit var recycler : RecyclerView
    private lateinit var adapter : CitySelectAdapter
    private lateinit var arrayList : ArrayList<CitySelectItem>

    override fun onDialogPositiveClick(city: String, country: String) {
        arrayList.add(CitySelectItem(arrayList.size, city, country, 0))
        val shared = context?.getSharedPreferences("MainActivity", MODE_PRIVATE) ?: return
        with(shared.edit()){
            val index = arrayList.size
            putInt("count", index)
            putString((index-1).toString(), "$city;$country;0;0;0;")
            commit()
        }
        update()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_weather_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(LocalTime.now().isBefore(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            view.findViewById<ConstraintLayout>(R.id.weatherLayout).setBackgroundResource(R.drawable.gradient)
        }
        else{
            view.findViewById<ConstraintLayout>(R.id.weatherLayout).setBackgroundResource(R.drawable.gradient_night)
        }

        recycler = view.findViewById(R.id.cityList)
        arrayList = ArrayList()
        val shared = context?.getSharedPreferences("MainActivity", MODE_PRIVATE) ?: return
        val toLoad = shared.getInt("count", 0)
        for(i in 0 until toLoad){
            val csv = shared.getString(i.toString(), "").toString()
            val info = csv.splitToSequence(";")
            arrayList.add(CitySelectItem(i, info.elementAt(0), info.elementAt(1), info.elementAt(2).toInt()))
        }

        val manager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler.layoutManager = manager

        update()

    }

    private fun update(){
        adapter = CitySelectAdapter(arrayList)
        recycler.adapter = adapter
        adapter.onItemClick = {
            (context as MainActivity).apiCall(it.index)

        }
    }
}