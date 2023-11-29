package com.example.weatherapp

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.time.LocalTime

class CityAddFragment : Fragment(), OnQueryTextListener {


    private lateinit var recycler : RecyclerView
    private lateinit var adapter : CityAddAdapter
    private lateinit var arrayList : ArrayList<CityAddItem>
    private val jsonParser = JSONParser()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(LocalTime.now().isBefore(LocalTime.of(MainActivity.SWAP_TIME, 0))){
            view.findViewById<ConstraintLayout>(R.id.searchFragmentRoot).setBackgroundResource(R.drawable.gradient)
        }
        else{
            view.findViewById<ConstraintLayout>(R.id.searchFragmentRoot).setBackgroundResource(R.drawable.gradient_night)
        }

        view.findViewById<SearchView>(R.id.searchView).setOnQueryTextListener(this)

        recycler = view.findViewById(R.id.cityAddList)
        arrayList = ArrayList()

        val manager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler.layoutManager = manager
        update()

       // arrayList = jsonParser.parseCityNames(json)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_add, container, false)
    }

    private fun update(){
        adapter = CityAddAdapter(arrayList)
        recycler.adapter = adapter

        adapter.onItemClick = {
            val shared = context?.getSharedPreferences("MainActivity", MODE_PRIVATE) ?: throw Exception()
            with(shared.edit()){
                val index = shared.getInt("count", -1) + 1

                putInt("count", index)
                putString((index-1).toString(), "${it.cityName};${it.country};0;0;0;false;")
                apply()
            }
            (context as MainActivity).findViewById<BottomNavigationView>(R.id.navBar).selectedItemId = R.id.bar1
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val name = query?.split(",")
        arrayList.add(CityAddItem(name?.get(0) ?: "none", name?.get(1) ?: "none"))
        update()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}