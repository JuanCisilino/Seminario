package com.avalith.seminariokotlin.ui.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.avalith.seminariokotlin.databinding.ActivityMainBinding
import com.avalith.seminariokotlin.extensions.clearPrefs
import com.avalith.seminariokotlin.extensions.signOut
import com.avalith.seminariokotlin.model.Post
import com.avalith.seminariokotlin.model.Weather
import com.avalith.seminariokotlin.ui.adapters.PostAdapter
import com.avalith.seminariokotlin.ui.post.PostActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

const val REQUEST = 100
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    companion object {
        fun start(activity: Activity){
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        getLocationRequest()
        setButtons()
        viewModel.getPosts()
        subscribeToLiveData()
    }

    private fun setButtons() {
        binding.addPostButton.setOnClickListener { PostActivity.start(this) }
        binding.logOutButton.setOnClickListener { onBackPressed() }
    }

    private fun subscribeToLiveData() {
        viewModel.weatherLiveData.observe(this) { setWeather(it) }
        viewModel.errorLiveData.observe(this) { showToast(it) }
        viewModel.dataLiveData.observe(this) { setAdapter(it) }
    }

    private fun setAdapter(list: List<Post>) {
        binding.postListrecyclerView.layoutManager = LinearLayoutManager(this)
        binding.postListrecyclerView.adapter = PostAdapter(list)
    }

    private fun setWeather(weather: Weather?) {
        weather?.let {
            binding.cardView.setWeather(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getLocationRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION), REQUEST)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST -> {
                if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                        viewModel.getWeather(this, it.result)
                    }
                } else {
                    viewModel.getWeather(null)
                }
            }
            else -> { viewModel.getWeather(null) }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        signOut()
        clearPrefs()
    }
}