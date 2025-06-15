package com.example.gas.domain.usecase

import com.example.gas.domain.model.EmergencyInfo
import com.example.gas.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class GetEmergencyInfosUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(): List<EmergencyInfo> {
        return emergencyInfoRepository.getEmergencyInfos()
    }
}