package com.yessorae.yabaltravel.data.source.remote.kakao.model

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDto(
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double
)
