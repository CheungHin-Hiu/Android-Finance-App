package com.example.androidfinanceapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Define Retrofit service for calling backend authentication endpoints
interface AuthApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    @POST("/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponse>
}

// Login response and request dataclass
@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("jwt_token") val token: String,
    @SerialName("external_id") val externalId: String
)

// Sign up request
@Serializable
data class SignupRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

// Refresh request and response
@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class RefreshResponse(
    @SerialName("new_token") val newToken: String
)
