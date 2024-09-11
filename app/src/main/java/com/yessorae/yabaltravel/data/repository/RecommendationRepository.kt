package com.yessorae.yabaltravel.data.repository

import com.yessorae.yabaltravel.data.source.remote.yabal.model.RegionInfo

interface RecommendationRepository {
    suspend fun getRecommendation(
        ctPrvnName: String,
        siGunGuNam: String,
        page: Int,
        size: Int
    ): List<RegionInfo>
}
