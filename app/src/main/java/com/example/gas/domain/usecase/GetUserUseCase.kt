package com.example.gas.domain.usecase

import com.example.gas.domain.model.User
import com.example.gas.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = true): User? {
        return userRepository.getUser(forceUpdate)
    }
}