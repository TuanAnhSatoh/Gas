package com.example.gas.domain.usecase.auth

import com.example.gas.data.source.network.datasource.AuthDataSource
import javax.inject.Inject

class LinkGoogleCredentialUseCase @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend operator fun invoke(idToken: String, email: String, password: String): Result<Unit> {
        return authDataSource.linkGoogleCredential(idToken, email, password)
    }
}
