package com.sunnyweather.android

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.logic.service.AutoRefresh

import com.sunnyweather.android.ui.weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*

import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    lateinit var  serviceIntent : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        serviceIntent = Intent(this,AutoRefresh::class.java)
        startService(serviceIntent)

        // 沉浸式状态栏
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT


        //获取内容
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        //监听 数据变化
        viewModel.weatherLiveData.observe(this, Observer {
            val weather = it.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()


        //下拉刷新监听器
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        //按钮监听
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)

        }
        //侧边栏监听
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            //当侧边栏关闭时隐藏输入法 清空输入框
            override fun onDrawerClosed(drawerView: View) {
                searchPlaceEdit.text.clear()
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })


    }




    //内容填充
    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTemperature = "${realtime.temperature.toInt()}℃"
        currentTemp.text = currentTemperature
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentSky.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            skyIcon.setImageResource(sky.icon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            skyInfo.text = sky.info
            val tempText = "${temperature.min}℃ ~ ${temperature.max}℃"
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            temperatureInfo.text = tempText
            forecastLayout.addView(view)

        }

        val lifeIndex = daily.lifeIndex

        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE


    }


    //刷新
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }



    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }

    }

