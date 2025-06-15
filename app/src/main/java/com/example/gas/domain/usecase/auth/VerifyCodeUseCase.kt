package com.example.gas.domain.usecase.auth

import com.example.gas.domain.repository.UserRepository
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, code: String) {
        userRepository.verifyCode(email, code)
    }
}