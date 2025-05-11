package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.AuthApiService
import com.example.androidfinanceapp.network.LoginRequest
import com.example.androidfinanceapp.network.LoginResponse
import com.example.androidfinanceapp.network.SignupRequest
import com.example.androidfinanceapp.network.SignupResponse
import retrofit2.Response

// Repository for ViewModel to call APIs related to authentication
interface AuthRepository {
    suspend fun login(username: String, password: String): Response<LoginResponse>

    suspend fun signup(username: String, password: String): Response<SignupResponse>

}

// For DP injection
class NetworkAuthRepository(
    private val authApiService: AuthApiService
): AuthRepository {
    override suspend fun login(username: String, password: String) =
        authApiService.login(LoginRequest(username, password))

    override suspend fun signup(username: String, password: String): Response<SignupResponse> =
        authApiService.signup(SignupRequest(username, password))
}