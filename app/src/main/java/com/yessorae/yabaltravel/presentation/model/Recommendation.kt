package com.yessorae.yabaltravel.presentation.model

import com.yessorae.yabaltravel.data.source.remote.yabal.model.RecommendationDto

data class Recommendation(
    val id: Int,
    val title: String
)

fun RecommendationDto.asDto() = Recommendation(
    id = id,
    title = name
)