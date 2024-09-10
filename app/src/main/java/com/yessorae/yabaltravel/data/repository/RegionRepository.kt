package com.yessorae.yabaltravel.data.repository

import com.yessorae.yabaltravel.data.source.remote.kakao.model.Document

interface RegionRepository {
    suspend fun getNearestRegions(
        lat: Double,
        lng: Double
    ): List<Document>
}

