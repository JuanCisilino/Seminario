package com.avalith.seminariokotlin.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avalith.seminariokotlin.R
import com.avalith.seminariokotlin.extensions.clearPrefs
import com.avalith.seminariokotlin.extensions.signOut

class MainActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity){
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        signOut()
        clearPrefs()
    }
}