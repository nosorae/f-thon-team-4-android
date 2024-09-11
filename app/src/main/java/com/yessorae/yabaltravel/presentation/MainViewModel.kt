package com.yessorae.yabaltravel.presentation

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
import com.yessorae.yabaltravel.presentation.model.asDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    private val regionRepository: RegionRepository
) : ViewModel() {
    private val _screenState =
        MutableStateFlow<MainScreenState>(BeforeThrowingState)
    val screenState = _screenState.asStateFlow()

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

    fun onClickGetRecommendation() {
        _screenState.update {
            RecommendationSuccessState(
                recommendation = recommendationRepository.getRecommendation().map { it.asDto() }
            )
        }
    }

    /**
     * [RecommendationSuccessState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onClickRecommendation(id: Int) {

    }


    /**
     * [RecommendationFailureState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
}