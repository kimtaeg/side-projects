package com.example.week10.api

import com.example.week10.data.LoginData
import com.example.week10.data.SignupData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRetrofitinterface {
    @POST("/signup")
    suspend fun signup(@Body req: SignUpRequest): Response<AuthResponse<SignupData>>

    @POST("/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse<LoginData>>
}