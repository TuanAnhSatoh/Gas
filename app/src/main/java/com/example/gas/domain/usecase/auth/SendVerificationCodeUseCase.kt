package com.example.gas.domain.usecase.auth

import com.example.gas.domain.repository.UserRepository
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String) {
        userRepository.sendVerificationCode(email)
    }
}