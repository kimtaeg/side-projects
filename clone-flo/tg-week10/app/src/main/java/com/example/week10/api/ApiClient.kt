package com.example.week10.api

//import com.kakao.sdk.v2.auth.BuildConfig
import com.example.week10.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val BASE_URL = BuildConfig.BASE_URL
    // 로그 진행상황
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //클라이언트 생성, 로그와 타임아웃 설정
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging).connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client).addConverterFactory(GsonConverterFactory.create())
        .build()

    //서비스 정의
    //val authService : AuthService = retrofit.create(AuthService::class.java))
    val authService: AuthRetrofitinterface = retrofit.create(AuthRetrofitinterface::class.java)
}