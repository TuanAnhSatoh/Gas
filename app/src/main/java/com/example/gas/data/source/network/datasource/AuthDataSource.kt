package com.example.gas.data.source.network.datasource

interface AuthDataSource {

    suspend fun loginUser(email: String, password: String): String

    suspend fun registerUser(email: String, password: String): String

    suspend fun googleSignIn(idToken: String): String

    suspend fun sendVerificationCode(email: String)

    suspend fun verifyCode(email: String, code: String)

    suspend fun resetPassword(email: String, newPassword: String)

    suspend fun updatePassword(email: String, currentPassword: String, newPassword: String)

    suspend fun logout()

    fun getCurrentUserId(): String?

    suspend fun deleteUser(string: String)

    suspend fun linkGoogleCredential(idToken: String, email: String, password: String): Result<Unit>
}