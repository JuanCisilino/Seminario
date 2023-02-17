package com.avalith.seminariokotlin.ui.home

import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avalith.seminariokotlin.model.Post
import com.avalith.seminariokotlin.model.Weather
import com.avalith.seminariokotlin.repositories.FirebaseRepo
import com.avalith.seminariokotlin.repositories.WeatherRepo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel: ViewModel() {

    val weatherLiveData = MutableLiveData<Weather>()
    val errorLiveData = MutableLiveData<String>()
    val dataLiveData = MutableLiveData<List<Post>>()

    private val weatherRepo = WeatherRepo()
    private lateinit var firebaseRepo : FirebaseRepo

    fun getWeather(context: Context?, location: Location?=null){
        context?.let { getWeatherData(geocode(it, location!!)) }
            ?:run { getWeatherData("Cordoba") }
    }

    private fun geocode(context: Context, location: Location): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return address?.get(0)?.adminArea ?:"Cordoba"
    }

    private fun getWeatherData(location: String) {
        val call: Call<Weather> = weatherRepo.getWeather(location)

        call.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                weatherLiveData.value = response.body()
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                errorLiveData.value = t.message
            }
        })
    }

    fun getPosts() {
        firebaseRepo = FirebaseRepo()
        val postList = ArrayList<Post>()
        firebaseRepo.getPosts().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { data ->
                    data.getValue(Post::class.java)?.let {
                        if (!postList.contains(it)) postList.add(it)
                    }
                }
                dataLiveData.value = postList.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                errorLiveData.value = error.message
            }
        })
    }
}