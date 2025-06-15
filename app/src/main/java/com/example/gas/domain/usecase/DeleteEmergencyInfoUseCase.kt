package com.example.gas.domain.usecase

import com.example.gas.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class DeleteEmergencyInfoUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(emergencyInfoId: String) {
        emergencyInfoRepository.deleteEmergencyInfo(emergencyInfoId)
    }
}