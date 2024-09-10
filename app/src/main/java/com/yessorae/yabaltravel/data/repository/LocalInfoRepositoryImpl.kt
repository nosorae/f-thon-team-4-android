package com.yessorae.yabaltravel.data.repository

import com.yessorae.yabaltravel.data.source.remote.kakao.api.KakaoLocalApi
import com.yessorae.yabaltravel.data.source.remote.kakao.model.Document
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    private val kakaoLocalApi: KakaoLocalApi
) : RegionRepository {
    override suspend fun getNearestRegions(lat: Double, lng: Double): List<Document> {
        return kakaoLocalApi.getRegionInfo(
            x = lat.toString(),
            y=  lng.toString()
        ).documents
    }
}