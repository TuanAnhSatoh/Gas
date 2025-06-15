package com.example.gas.domain.usecase

import com.example.gas.domain.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Use case for updating a user's profile information.
 */
class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        phone: String
    ) {
        val formattedDate = if (dateOfBirth.isNotEmpty()) {
            try {
                val userFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dbFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val date = LocalDate.parse(dateOfBirth, userFormatter)
                date.format(dbFormatter)
            } catch (e: Exception) {
                throw Exception("Invalid date of birth format: $dateOfBirth. Expected dd/MM/yyyy")
            }
        } else {
            dateOfBirth
        }

        userRepository.updateUser(
            userId = userId,
            name = name,
            address = address,
            dateOfBirth = formattedDate,
            gender = gender,
            phone = phone
        )
    }
}