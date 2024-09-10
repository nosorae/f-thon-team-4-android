package com.yessorae.yabaltravel.data.source.remote.kakao.api

import com.yessorae.yabaltravel.data.source.remote.kakao.model.RegionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLocalApi {
    @GET("v2/local/geo/coord2regioncode.json")
    suspend fun getRegionInfo(
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("input_coord") inputCoord: String = "WGS84",
        @Query("output_coord") outputCoord: String = "WGS84"
    ): RegionResponse
}
