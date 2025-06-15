package com.example.gas.domain.usecase

import com.example.gas.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class CreateEmergencyInfoUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(
        contactName: String,
        contactNumber: String,
        priority : Int
    ): String {
        return emergencyInfoRepository.createEmergencyInfo(
            contactName = contactName,
            contactNumber = contactNumber,
            priority = priority
        )
    }
}