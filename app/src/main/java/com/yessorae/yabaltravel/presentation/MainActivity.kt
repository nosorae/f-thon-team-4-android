package com.yessorae.yabaltravel.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.yessorae.yabaltravel.BuildConfig
import com.yessorae.yabaltravel.R
import com.yessorae.yabaltravel.databinding.ActivityIntroBinding
import com.yessorae.yabaltravel.databinding.ActivityMainBinding
import com.yessorae.yabaltravel.presentation.model.ShakerDetector
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ShakerDetector.OnShareListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakerDetector
    private val viewModel : MainViewModel by viewModels()
    private val kakaoKey = "1c5de4030ea347c753fcf2c26b656b58"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        addListener()
    }

    private fun init() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakerDetector(this)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
        KakaoMapSdk.init(this ,kakaoKey)
    }

    private fun addListener(){
        binding.mapKakao.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d(this.javaClass.name , "kakao Map is Destroy")
            }

            override fun onMapError(error: Exception) {
                Log.d(this.javaClass.name , "Map is Error")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                Log.d(this.javaClass.name , "map is Ready")
            }
        })
    }

    override fun onShake() {
        Log.d(this.javaClass.name, "Device Shaken!")
//        viewModel.onThrowing()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(shakeDetector)
    }
}