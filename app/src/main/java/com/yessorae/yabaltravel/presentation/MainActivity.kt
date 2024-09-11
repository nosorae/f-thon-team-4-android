package com.yessorae.yabaltravel.presentation

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.yessorae.yabaltravel.R
import com.yessorae.yabaltravel.databinding.ActivityMainBinding
import com.yessorae.yabaltravel.presentation.model.MainScreenState
import com.yessorae.yabaltravel.presentation.model.ShakerDetector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ShakerDetector.OnShareListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakerDetector
    private val viewModel: MainViewModel by viewModels()
    private val kakaoKey = "1c5de4030ea347c753fcf2c26b656b58"
    private val startPosition: LatLng = LatLng.from(37.394660, 127.111182)
    private val startZoomLevel = 15
    private var kakaoMap: KakaoMap? = null
    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            Toast.makeText(applicationContext, "Map Start!", Toast.LENGTH_SHORT).show()
            this@MainActivity.kakaoMap = kakaoMap
        }

        override fun getPosition(): LatLng {
            return startPosition
        }

        override fun getZoomLevel(): Int {
            return startZoomLevel
        }
    }
    private val lifeCycleCallback: MapLifeCycleCallback = object : MapLifeCycleCallback() {
        override fun onMapResumed() {
            super.onMapResumed()
        }

        override fun onMapPaused() {
            super.onMapPaused()
        }

        override fun onMapDestroy() {
            Toast.makeText(
                applicationContext, "onMapDestroy",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onMapError(error: java.lang.Exception) {
            viewModel.onThrowing() // TODO Delete
            Toast.makeText(
                applicationContext, error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
        KakaoMapSdk.init(this, kakaoKey)
        binding.mapKakao.start(lifeCycleCallback, readyCallback)
        observe()
    }

    private fun setMarker() {

    }

    private fun addListener() {

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                viewModel.screenState.collectLatest { handleScreenState(it) }
            }
        }
    }

    private fun handleScreenState(screenState: MainScreenState) {
        when (screenState) {
            is MainScreenState.BeforeThrowingState -> {
            }

            is MainScreenState.AfterThrowingState -> {
                /**
                 * Label(지도위에 마커) 예시 코드 입니다!
                 */
                val map = kakaoMap ?: return

                val latLng =  LatLng.from(
                    screenState.x.toDouble(),
                    screenState.y.toDouble()
                )
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(latLng.longitude, latLng.latitude)));
                val pos = map.cameraPosition?.position

                val labelManager = map.labelManager
                val thumbsUpStyle = labelManager!!.addLabelStyles(
                    LabelStyles.from("thumbsUp", LabelStyle.from(R.drawable.pink_marker))
                )
                labelManager.layer!!.addLabel(
                    LabelOptions.from("label", latLng)
                        .setStyles(thumbsUpStyle)
                )
            }

            is MainScreenState.RecommendationSuccessState -> {
            }

            is MainScreenState.RecommendationFailureState -> {
            }
        }
    }

    override fun onShake() {
        Log.d(this.javaClass.name, "Device Shaken!")
        viewModel.onThrowing()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(shakeDetector)
    }
}