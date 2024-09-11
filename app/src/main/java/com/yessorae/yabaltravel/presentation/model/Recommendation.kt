package com.yessorae.yabaltravel.presentation.model

import androidx.core.view.ContentInfoCompat.Flags


data class Recommendation(
    val name: String,
    val description: String,
    val longitude : Double,
    val latitude : Double
)
data class TestCode(
    val x : Double,
    val y : Double
)