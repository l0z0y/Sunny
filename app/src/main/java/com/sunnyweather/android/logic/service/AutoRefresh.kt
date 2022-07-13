package com.sunnyweather.android.logic.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.sunnyweather.android.WeatherActivity

class AutoRefresh : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Service", "Create: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        Toast.makeText(this, "alarm running", Toast.LENGTH_SHORT).show()
        Log.d("Service", "StartCommand")

        //定时器 发送广播
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val time = 10 * 1000
        val l = SystemClock.elapsedRealtime() + time
        val i = Intent(this, ServiceReceiver::class.java)
        val broadcast = PendingIntent.getBroadcast(this, 0, i, 0)
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, l, broadcast)

        return super.onStartCommand(intent, flags, startId)


    }

    override fun onDestroy() {
        super.onDestroy()

    }
}