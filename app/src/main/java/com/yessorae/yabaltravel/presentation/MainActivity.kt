package com.yessorae.yabaltravel.presentation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.yessorae.yabaltravel.R
import com.yessorae.yabaltravel.common.BottomSheetListener
import com.yessorae.yabaltravel.common.Define
import com.yessorae.yabaltravel.databinding.ActivityMainBinding
import com.yessorae.yabaltravel.presentation.model.MainScreenState
import com.yessorae.yabaltravel.presentation.model.RecommendBottomData
import com.yessorae.yabaltravel.presentation.model.RecommendItem
import com.yessorae.yabaltravel.presentation.model.ShakerDetector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BottomSheetListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakerDetector
    private val viewModel: MainViewModel by viewModels()
    private val kakaoKey = "1c5de4030ea347c753fcf2c26b656b58"
    private val startPosition: LatLng = LatLng.from(37.394660, 127.111182)
    private val defaultZoomLevel = 6
    private var kakaoMap: KakaoMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                getLastKnownLocation()
            } else {
                finish()
            }
        }
    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            Toast.makeText(applicationContext, "Map Start!", Toast.LENGTH_SHORT).show()
            this@MainActivity.kakaoMap = kakaoMap
        }

        override fun getPosition(): LatLng {
            return startPosition
        }

        override fun getZoomLevel(): Int {
            return defaultZoomLevel
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
            Toast.makeText(
                applicationContext, error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observe()
    }

    private fun initViews() {
        initButtons()
        initKakaoMap()
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initButtons() {
        binding.btnRetry.setOnClickListener {
            viewModel.onClickRetryThrowing()
        }

        binding.btnRecommendation.setOnClickListener {
            viewModel.onClickGetRecommendation()
        }
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
                resetKakaoMap()
                initSensor()
            }

            is MainScreenState.AfterThrowingState -> {
                /**
                 * Label(지도위에 마커) 예시 코드 입니다!
                 */
                val map = kakaoMap ?: return

                val latLng = LatLng.from(
                    screenState.lng,
                    screenState.lat
                )

                val labelManager = map.labelManager

                val thumbsUpStyle = labelManager!!.addLabelStyles(
                    LabelStyles.from(
                        "thumbsUp",
                        LabelStyle.from(R.drawable.pink_marker)
                    )
                )

                labelManager.layer!!.addLabel(
                    LabelOptions.from("label", latLng)
                        .setStyles(thumbsUpStyle)
                )
            }

            is MainScreenState.RecommendationSuccessState -> {
                val result = screenState.recommendation
                val longitude = result.map { it.longitude }.average()
                val latitude = result.map { it.latitude }.average()
                kakaoMap?.moveCamera(
                    CameraUpdateFactory.newCenterPosition(
                        LatLng.from(
                            latitude,
                            longitude
                        ),
                        11
                    )
                )
                for (item in result) {
                    val styles = kakaoMap!!.labelManager
                        ?.addLabelStyles(
                            LabelStyles.from(LabelStyle.from(R.drawable.pink_marker))
                        )
                    val options = LabelOptions.from(LatLng.from(item.latitude, item.longitude))
                        .setStyles(styles)
                    val layer = kakaoMap!!.labelManager!!.layer
                    val label = layer!!.addLabel(options)
                }

                val recommendData = viewModel.makeRecommendData(result, this)
                val bottomSheet = RecommendBottomSheet(recommendData, this)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
            is MainScreenState.Error ->{
                Toast.makeText(this , screenState.message , Toast.LENGTH_SHORT).show()
            }
            else ->{
                //do noting
            }
        }

        binding.btnRetry.isVisible = screenState is MainScreenState.AfterThrowingState
        binding.btnRecommendation.isVisible = screenState is MainScreenState.AfterThrowingState
        binding.progressBar.isVisible = screenState is MainScreenState.LoadingState
    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakerDetector {
            Log.d(this.javaClass.name, "Device Shaken!")
            viewModel.onThrowing()
        //            sensorManager.unregisterListener(shakeDetector)
        }
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(this, kakaoKey)
        binding.mapKakao.start(lifeCycleCallback, readyCallback)
    }

    private fun resetKakaoMap() {
        val labelManager = kakaoMap?.labelManager ?: return
        labelManager.clearAll()
        kakaoMap?.moveCamera(
            CameraUpdateFactory.newCenterPosition(
                LatLng.from(
                    startPosition.latitude,
                    startPosition.longitude
                ),
                defaultZoomLevel
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(shakeDetector)
    }

    override fun onBottomSheetDismissed(resultCode: RecommendBottomData) {
        when (resultCode.resultCode) {
            Define.BOTTOM_SHEET_SELECT -> {
                Log.d(this.javaClass.name, "User select go to Trip")
                searchLoadToKakaoMap(resultCode.item!!)
            }

            Define.BOTTOM_SHEET_NO -> {
                Log.e(this.javaClass.name, "User select reTry")
                resetKakaoMap()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // 여기서 좌표를 사용
                    viewModel.setLocation(latitude, longitude)
                    Log.d("MainActivity", "Latitude: $latitude, Longitude: $longitude")
                }
            }
    }

    private fun searchLoadToKakaoMap(data : RecommendItem) {
        val url =
            "kakaomap://route?sp=${viewModel.getLocation().first},${viewModel.getLocation().second}&ep=${data.latitude},${data.longitude}&by=CAR"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        val installCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            this.packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.GET_META_DATA
            )
        }
        if (installCheck.isEmpty()) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=net.daum.android.map")
                )
            )
        } else {
            startActivity(intent)
        }
    }

}