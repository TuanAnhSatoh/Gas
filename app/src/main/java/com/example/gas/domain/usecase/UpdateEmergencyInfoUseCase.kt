package com.example.gas.domain.usecase

import com.example.gas.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class UpdateEmergencyInfoUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(
        emergencyInfoId: String,
        contactName: String,
        contactNumber: String,
        priority : Int
    ) {
        emergencyInfoRepository.updateEmergencyInfo(
            emergencyInfoId = emergencyInfoId,
            contactName = contactName,
            contactNumber = contactNumber,
            priority = priority
        )
    }
}