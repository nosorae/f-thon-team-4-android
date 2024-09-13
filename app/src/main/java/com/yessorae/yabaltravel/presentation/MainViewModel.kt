package com.yessorae.yabaltravel.presentation

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yessorae.yabaltravel.common.MapConstants
import com.yessorae.yabaltravel.data.repository.RecommendationRepository
import com.yessorae.yabaltravel.data.repository.RegionRepository
import com.yessorae.yabaltravel.data.source.remote.kakao.model.Document
import com.yessorae.yabaltravel.presentation.model.MainScreenState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.AfterThrowingState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.BeforeThrowingState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.RecommendationFailureState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.RecommendationSuccessState
import com.yessorae.yabaltravel.presentation.model.RecommendItem
import com.yessorae.yabaltravel.presentation.model.Recommendation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val recommendationRepository: RecommendationRepository,
    private val regionRepository: RegionRepository
) : ViewModel() {
    private val _screenState =
        MutableStateFlow<MainScreenState>(BeforeThrowingState)
    val screenState = _screenState.asStateFlow()

    private val testCode = ArrayList<Recommendation>()
    private lateinit var geocoder: Geocoder
    private lateinit var recommendation: Recommendation
    private var latitude = 0.0
    private var longitude = 0.0
    var throwAgain = true
    private set

    private var currentRandomLocation: Document? = null

    /**
     * [BeforeThrowingState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onThrowing() = viewModelScope.launch {
        val randomKoreaLng = Random.nextDouble(
            from = MapConstants.KOREA_LNG_MIN,
            until = MapConstants.KOREA_LNG_MAX
        )
        val randomKoreaLat = Random.nextDouble(
            from = MapConstants.KOREA_LAT_MIN,
            until = MapConstants.KOREA_LAT_MAX
        )

        val response = regionRepository.getNearestRegions(
            lat = randomKoreaLat,
            lng = randomKoreaLng
        ).firstOrNull()

        response ?: return@launch

        _screenState.update {
            AfterThrowingState(
                lat = response.x,
                lng = response.y
            )
        }
    }


    /**
     * [AfterThrowingState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onClickRetryThrowing() {
        _screenState.update { BeforeThrowingState }
    }

    fun makeRecommendData(
        data: List<Recommendation>,
        context: Context
    ): ArrayList<RecommendItem> {
        geocoder = Geocoder(context)
        var result = ArrayList<RecommendItem>()
        for (item in data) {
            val address = geocoder.getFromLocation(item.latitude, item.longitude, 1)
            if (address == null) {
                result.add(
                    RecommendItem(
                        item.name,
                        item.description,
                        "NULL",
                        item.longitude,
                        item.latitude
                    )
                )
                continue
            }
            result.add(
                RecommendItem(
                    item.name,
                    item.description,
                    address[0].getAddressLine(0),
                    item.longitude,
                    item.latitude
                )
            )
        }
        return result
    }

    fun onClickGetRecommendation() = viewModelScope.launch {
        val current = currentRandomLocation ?: return@launch

        val response = recommendationRepository.getRecommendation(
            ctPrvnName = current.region1DepthName,
            siGunGuNam = current.region2DepthName,
            page = 0,
            size = 10
        )

        val result = ArrayList<Recommendation>()
        for (item in response) {
            result.add(
                Recommendation(
                    item.name,
                    item.description,
                    item.longitude,
                    item.latitude
                )
            )
        }
        recommendation = result[result.lastIndex]

        _screenState.update {
            RecommendationSuccessState(
                recommendation = result
            )
        }
    }

    fun getRecommend() = recommendation

    fun setLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun getLocation(): Pair<Double, Double> = Pair(latitude, longitude)

    /**
     * [RecommendationSuccessState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onClickRecommendation(id: Int) {

    }


    /**
     * [RecommendationFailureState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */



    fun setTrowAgain(value : Boolean){
        throwAgain = value
    }
}