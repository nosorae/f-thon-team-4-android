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
    var throwAgain = true
    private set

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
        testCode.add(Recommendation("F-Lab Cafe", "원미산 산림욕장은 경기도 부천시 춘의동 산39-1번지에 위치한 자연공원으로, 약 3만평 규모의 면적을 가지고 있습니다.\n" +
                "이곳은 다양한 나무와 식물들이 서식하고 있어 자연을 즐길 수 있는 곳으로, 산책로와 등산로가 잘 조성되어 있어 가족이나 연인, 친구들과 함께 산책을 즐기기에 좋은 장소입니다.\n" +
                "또한, 봄에는 진달래 축제가 열리며 여름에는 물놀이장, 가을에는 단풍축제, 겨울에는 얼음축제가 열리는 등 계절마다 다양한 이벤트가 열려 많은 사람들이 찾는 곳입니다.\n" +
                "원미산 산림욕장 내에는 정자, 벤치, 운동시설 등이 마련되어 있어 휴식을 취하기에도 좋으며, 주변에는 부천종합운동장, 부천시청, 부천중앙공원 등의 관광명소가 있어 함께 방문하기에도 좋습니다.\n" +
                "원미산 산림욕장은 대중교통을 이용하여 쉽게 접근할 수 있으며, 주차장도 마련되어 있으니 차를 이용하시는 분들도 편리하게 이용하실 수 있습니다.", 127.111182,37.394660, ))
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



    fun setTrowAgain(value : Boolean){
        throwAgain = value
    }
}