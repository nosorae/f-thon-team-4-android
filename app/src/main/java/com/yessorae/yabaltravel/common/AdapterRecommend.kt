package com.yessorae.yabaltravel.common

import com.yessorae.yabaltravel.presentation.model.RecommendItem

interface AdapterRecommend {
    fun setOnClickListener(data : RecommendItem)
}