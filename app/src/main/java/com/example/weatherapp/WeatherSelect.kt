package com.example.weatherapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
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

class WeatherSelect : AppCompatActivity(), AddCityFragment.DialogListener {

    private lateinit var recycler : RecyclerView
    private lateinit var adapter : CitySelectAdapter
    private lateinit var arrayList : ArrayList<CitySelectItem>

    override fun onDialogPositiveClick(city: String, country: String) {
        arrayList.add(CitySelectItem(arrayList.size, city, country, 0))
        val shared = getSharedPreferences("MainActivity", MODE_PRIVATE)
        with(shared.edit()){
            val index = arrayList.size
            putInt("count", index)
            putString((index-1).toString(), "$city;$country;0;0;0;")
            commit()
        }
        update()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menuAdd -> {
                AddCityFragment().show(supportFragmentManager, "city")
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu_bar, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_select)
        if(LocalTime.now().isBefore(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            findViewById<ConstraintLayout>(R.id.weatherLayout).setBackgroundResource(R.drawable.gradient)
        }
        else{
            findViewById<ConstraintLayout>(R.id.weatherLayout).setBackgroundResource(R.drawable.gradient_night)
        }

        recycler = findViewById(R.id.cityList)
        arrayList = ArrayList()
        val shared = getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
        val toLoad = shared.getInt("count", 0)
        for(i in 0 until toLoad){
            val csv = shared.getString(i.toString(), "").toString()
            val info = csv.splitToSequence(";")
            arrayList.add(CitySelectItem(i, info.elementAt(0), info.elementAt(1), info.elementAt(2).toInt()))
        }

        val manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.layoutManager = manager

        update()

    }

    private fun update(){
        adapter = CitySelectAdapter(arrayList)
        recycler.adapter = adapter
        adapter.onItemClick = {
            val tmp = Intent()
            tmp.putExtra("activity", 0)
            tmp.putExtra("data", it.index)
            setResult(RESULT_OK, tmp)
            finish()
        }
    }
}