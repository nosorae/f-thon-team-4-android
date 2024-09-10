package com.yessorae.yabaltravel.data.source.remote.kakao.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionResponse(
    val meta: Meta,
    val documents: List<Document>
)

@Serializable
data class Meta(
    @SerialName("total_count")
    val totalCount: Int
)

@Serializable
data class Document(
    @SerialName("region_type")
    val regionType: String,
    @SerialName("address_name")
    val addressName: String,
    @SerialName("region_1depth_name")
    val region1DepthName: String,
    @SerialName("region_2depth_name")
    val region2DepthName: String,
    @SerialName("region_3depth_name")
    val region3DepthName: String,
    @SerialName("region_4depth_name")
    val region4DepthName: String,
    val code: String,
    val x: Double,
    val y: Double
)