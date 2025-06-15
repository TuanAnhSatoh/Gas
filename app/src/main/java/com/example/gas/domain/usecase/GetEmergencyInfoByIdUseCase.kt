package com.example.gas.domain.usecase

import com.example.gas.domain.model.EmergencyInfo
import javax.inject.Inject
import com.example.gas.domain.repository.EmergencyInfoRepository

class GetEmergencyInfoByIdUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(emergencyInfoId: String): EmergencyInfo? {
        return emergencyInfoRepository.getEmergencyInfo(emergencyInfoId)
    }
}