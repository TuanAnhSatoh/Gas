package com.example.gas.domain.usecase


import com.example.gas.domain.repository.MeasurementRepository
import javax.inject.Inject

class GetMeasurementRealTimeUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    operator fun invoke() = measurementRepository.getMeasurementsStream()
}