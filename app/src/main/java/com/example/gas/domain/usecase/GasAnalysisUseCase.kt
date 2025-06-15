package com.example.gas.domain.usecase

import javax.inject.Inject

class GasAnalysisUseCase @Inject constructor() {
    fun isAbnormal(gas: Float): Boolean {
        return gas < 93.0f
    }
}