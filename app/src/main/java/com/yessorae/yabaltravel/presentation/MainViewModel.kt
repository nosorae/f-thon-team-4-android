package com.yessorae.yabaltravel.presentation

import androidx.lifecycle.ViewModel
import com.yessorae.yabaltravel.common.MapConstants
import com.yessorae.yabaltravel.data.repository.RecommendationRepository
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
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {
    private val _screenState =
        MutableStateFlow<MainScreenState>(BeforeThrowingState)
    val screenState = _screenState.asStateFlow()

    /**
     * [BeforeThrowingState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onThrowing() {
        _screenState.update {
            AfterThrowingState(
                x = Random.nextDouble(
                    from = MapConstants.KOREA_LATITUDE_MIN,
                    until = MapConstants.KOREA_LATITUDE_MAX
                ).toFloat(),
                y = Random.nextDouble(
                    from = MapConstants.KOREA_LONGITUDE_MIN,
                    until = MapConstants.KOREA_LONGITUDE_MAX
                ).toFloat()
            )
        }
    }


    /**
     * [AfterThrowingState] 상태에서 추가하는 함수는 여기에 추가해주세요
     */
    fun onClickRetryThrowing() {
        _screenState.update { BeforeThrowingState }
    }

    fun onClickRecommendation() {
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