package com.example.week10

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this,"de89ade7de5c3826bf3050bc2c3f433d")
    }
}