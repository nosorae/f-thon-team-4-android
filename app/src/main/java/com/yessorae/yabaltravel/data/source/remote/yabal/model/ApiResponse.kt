package com.yessorae.yabaltravel.data.source.remote.yabal.model

import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: List<RegionInfo>,
    val pageNumber: Int,
    val pageSize: Int
)

@Serializable
data class RegionInfo(
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double
)