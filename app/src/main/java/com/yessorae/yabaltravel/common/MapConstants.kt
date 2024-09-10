package com.yessorae.yabaltravel.common

object MapConstants {
    // 한반도의 위치는 북위 33~43, 동경 124~132
    // TODO 북한 제외 필요
    // TODO 값 조절 필요, {"code":-2,"msg":"The input parameter value is not in the service area"} 발생
    const val KOREA_LNG_MIN = 34.0
    const val KOREA_LNG_MAX = 42.0
    const val KOREA_LAT_MIN = 125.0
    const val KOREA_LAT_MAX = 131.0
}