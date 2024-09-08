package com.yessorae.yabaltravel.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yessorae.yabaltravel.data.repository.SetUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(private val setUpRepository: SetUpRepository) :
    ViewModel() {
    private var _liveData = MutableLiveData<Boolean>()
    val liveData: LiveData<Boolean> = _liveData

    fun getSettingValue() {
        val internetResult = setUpRepository.isInternetActive()
        if (!internetResult) {
            _liveData.value = false
            return
        }
        val sensorResult = setUpRepository.hasGyroSensor()
        if (!sensorResult) {
            _liveData.value = false
            return
        }
        _liveData.value = true
    }
}