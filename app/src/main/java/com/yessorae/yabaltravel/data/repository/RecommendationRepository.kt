package com.yessorae.yabaltravel.data.repository

import com.yessorae.yabaltravel.data.model.RecommendationDto

interface RecommendationRepository {
    fun  getRecommendation(): List<RecommendationDto>
}
