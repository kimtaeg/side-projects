package com.example.week10

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

// 로그인 상태를 저장하고 불러오는 간단한 관리 클래스
object AuthManager {
    private const val AUTH_PREFS = "auth_prefs"
    private const val KEY_LOGIN_TOKEN = "login_token"

    // 로그인 상태 저장 (액세스 토큰 저장)
    fun setLoginToken(context: Context, token: String) {
        val spf = context.getSharedPreferences(AUTH_PREFS, AppCompatActivity.MODE_PRIVATE)
        spf.edit().putString(KEY_LOGIN_TOKEN, token).apply()
    }

    // 로그인 상태 확인 (토큰이 있으면 로그인 상태)
    fun isLoggedIn(context: Context): Boolean {
        val spf = context.getSharedPreferences(AUTH_PREFS, AppCompatActivity.MODE_PRIVATE)
        return spf.getString(KEY_LOGIN_TOKEN, null) != null
    }

    // 로그아웃 처리
    fun logout(context: Context) {
        val spf = context.getSharedPreferences(AUTH_PREFS, AppCompatActivity.MODE_PRIVATE)
        spf.edit().remove(KEY_LOGIN_TOKEN).apply()
    }
}
