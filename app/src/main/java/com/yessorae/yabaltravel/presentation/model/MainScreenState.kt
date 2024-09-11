package com.yessorae.yabaltravel.presentation.model

sealed class MainScreenState {
    // 초기화 작업
    data object BeforeThrowingState : MainScreenState()

    // 던지는 모션 감지하면 이 상태로 전환
    data class AfterThrowingState(
        val lat: Double,
        val lng: Double
    ) : MainScreenState()

    // 추천 목록을 가져오는데 성공하면 이 상태로 전환
    data class RecommendationSuccessState(
        val recommendation: List<Recommendation>
    ) : MainScreenState()

    // 추천 목록을 가져오는데 실패하면 이 상태로 전환
    data class RecommendationFailureState(
        val errorMessage: String
    ) : MainScreenState()
}
