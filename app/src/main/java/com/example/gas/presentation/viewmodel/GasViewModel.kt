package com.example.gas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gas.domain.model.Measurement
import com.example.gas.domain.usecase.GetMeasurementRealTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GasViewModel @Inject constructor(
    private val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase
) : ViewModel() {

    private val _heartRateHistory = MutableLiveData<List<Measurement>>()
    val heartRateHistory: LiveData<List<Measurement>> get() = _heartRateHistory

    private val maxDataPoints = 20

    init {
        viewModelScope.launch {
            getMeasurementRealTimeUseCase().collectLatest { measurements ->
                val sortedMeasurements = measurements.sortedBy { it.dateTime }
                val latestMeasurements = if (sortedMeasurements.size > maxDataPoints) {
                    sortedMeasurements.takeLast(maxDataPoints)
                } else {
                    sortedMeasurements
                }
                _heartRateHistory.postValue(latestMeasurements)
            }
        }
    }
}