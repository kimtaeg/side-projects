package com.example.week9.api

// t를 사용하여 제너릭 클래스로 제너릭 클래스는 더 유연하고 유지보수하기 쉬운 네트워킹 통신으르 해준다.
data class AuthResponse<T>(
    val status: Boolean,
    val code:String,
    val message:String,
    val data: T? = null)

data class LoginRequest(
    //val name: String,
    val email: String,
    val password: String//토큰 저장을 만들기
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String
)