package com.yessorae.yabaltravel.presentation

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yessorae.yabaltravel.common.MapConstants
import com.yessorae.yabaltravel.data.repository.RecommendationRepository
import com.yessorae.yabaltravel.data.repository.RegionRepository
import com.yessorae.yabaltravel.presentation.model.MainScreenState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.AfterThrowingState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.BeforeThrowingState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.RecommendationFailureState
import com.yessorae.yabaltravel.presentation.model.MainScreenState.RecommendationSuccessState
import com.yessorae.yabaltravel.presentation.model.RecommendItem
import com.yessorae.yabaltravel.presentation.model.Recommendation
import com.yessorae.yabaltravel.presentation.model.TestCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun testCode() {
        testCode.add(Recommendation("F-Lab Cafe", "카페", 127.111182,37.394660, ))
        testCode.add(Recommendation("F-Lab 모각코", "음식점", 127.111182,37.395660 ))
        testCode.add(Recommendation("F-Lab FTone", "관광지", 127.111182,37.393660))
        testCode.add(Recommendation("집", "숙박", 127.111182,37.396660))
        _screenState.update {
            recommendation = testCode[testCode.lastIndex]
            RecommendationSuccessState(
                recommendation = testCode
            )
        }
    }

    fun makeRecommendData(
        data: List<Recommendation>,
        context: Context
    ): ArrayList<RecommendItem> {
        geocoder = Geocoder(context)
        var result = ArrayList<RecommendItem>()
        for (item in data) {
            val address = geocoder.getFromLocation( item.latitude , item.longitude, 1)
            if (address == null) {
                result.add(
                    RecommendItem(
                        item.name,
                        item.description,
                        "NULL"
                    )
                )
                continue
            }
            result.add(
                RecommendItem(
                    item.name,
                    item.description,
                    address[0].getAddressLine(0)
                )
            )
        }
        return result
    }

    fun onClickGetRecommendation(
        ctPrvnName: String,
        siGunGuNam: String,
        page: Int,
        size: Int
    ) = viewModelScope.launch {
        _screenState.update {
            val response =
                recommendationRepository.getRecommendation(ctPrvnName, siGunGuNam, page, size)
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
            RecommendationSuccessState(
                recommendation = result
            )
        }
    }

    fun getRecommend() = recommendation

    fun setLocation(latitude : Double , longitude : Double){
        this.latitude = latitude
        this.longitude = longitude
    }
    fun getLocation() : Pair<Double , Double> = Pair(latitude , longitude)
    /**
     * [RecommendationSuccessState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onClickRecommendation(id: Int) {

    }


    /**
     * [RecommendationFailureState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */

}