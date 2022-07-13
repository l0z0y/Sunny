package com.sunnyweather.android.logic.service

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.WeatherActivity
import com.sunnyweather.android.ui.weather.WeatherViewModel

class ServiceReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        try {
            val i = Intent(context, AutoRefresh::class.java)
            context.startService(i)

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}