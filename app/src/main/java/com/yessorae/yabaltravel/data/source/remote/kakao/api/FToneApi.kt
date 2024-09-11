package com.yessorae.yabaltravel.data.source.remote.kakao.api

import com.yessorae.yabaltravel.data.source.remote.yabal.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface FToneApi {
    @GET("/destinations")
    suspend fun getRegionInfo(
        @Query("ctPrvnName") ctPrvnName: String,
        @Query("siGunGuNam") siGunGuNam: String,
        @Query("page") page : Int,
        @Query("size") size : Int
    ): ApiResponse

}