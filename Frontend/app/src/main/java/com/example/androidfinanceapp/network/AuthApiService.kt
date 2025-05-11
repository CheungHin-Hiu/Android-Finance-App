package com.example.androidfinanceapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Define Retrofit service for calling backend authentication endpoints
interface AuthApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/register")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>
}

// Login response and request dataclass
@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val userName: String,
    @SerialName("token") val token: String,
)

// Sign up request
@Serializable
data class SignupRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class SignupResponse(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val userName: String,
    @SerialName("token") val token: String,
)

