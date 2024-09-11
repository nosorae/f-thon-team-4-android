package com.yessorae.yabaltravel.data.repository

import com.yessorae.yabaltravel.data.source.remote.kakao.api.FToneApi
import com.yessorae.yabaltravel.data.source.remote.kakao.api.KakaoLocalApi
import com.yessorae.yabaltravel.data.source.remote.yabal.model.ApiResponse
import com.yessorae.yabaltravel.data.source.remote.yabal.model.RegionInfo
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(private val fTonApi: FToneApi) :
    RecommendationRepository {

    override suspend fun getRecommendation(
        ctPrvnName: String,
        siGunGuNam: String,
        page: Int,
        size: Int
    ): List<RegionInfo> {
        return fTonApi.getRegionInfo(
            ctPrvnName, siGunGuNam, page, size
        ).data
    }
}
