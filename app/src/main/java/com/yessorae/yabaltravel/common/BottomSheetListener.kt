package com.yessorae.yabaltravel.common

import com.yessorae.yabaltravel.presentation.model.RecommendBottomData

interface BottomSheetListener {
    fun onBottomSheetDismissed(resultCode: RecommendBottomData)
}