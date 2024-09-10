package com.yessorae.yabaltravel.data.repository

import com.yessorae.yabaltravel.data.source.remote.yabal.model.RecommendationDto
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(): RecommendationRepository {
    override fun getRecommendation(): List<RecommendationDto> {
        // TODO yabal 서버에서 가져오기=
        return listOf(
            RecommendationDto(1, 37.5665f, 126.9780f, "Seoul"),
            RecommendationDto(2, 35.6895f, 139.6917f, "Tokyo"),
            RecommendationDto(3, 40.7128f, 74.0060f, "New York"),
            RecommendationDto(4, 51.5074f, 0.1278f, "London"),
            RecommendationDto(5, 48.8566f, 2.3522f, "Paris"),
            RecommendationDto(6, 55.7558f, 37.6176f, "Moscow"),
            RecommendationDto(7, 35.6895f, 51.3890f, "Tehran"),
            RecommendationDto(8, 23.8103f, 90.4125f, "Dhaka"),
            RecommendationDto(9, 28.6139f, 77.2090f, "New Delhi"),
            RecommendationDto(10, 6.5244f, 3.3792f, "Lagos")
        )
    }
}
