package com.yessorae.yabaltravel.presentation.model

import com.yessorae.yabaltravel.data.source.remote.kakao.model.RecommendationDto

fun RecommendationDto.asDomainModel() = Recommendation(
    name = name,
    description = description,
    longitude = longitude,
    latitude = latitude
)