package com.example.week10.ui.login

import com.example.week10.ui.main.MainActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.week10.AuthManager
import com.example.week10.ui.signup.SignUpActivity
import com.example.week10.api.AuthViewModel
import com.example.week10.databinding.ActivityLoginBinding
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient




class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        //회원가입 프리그먼트 이동
        binding.loginSignupTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginInBtn.setOnClickListener {
            login()
            observeLoginResult()
        }

        var keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        binding.loginKakakoLoginIv.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("KAKAO_LOGIN", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("KAKAO_LOGIN", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    AuthManager.setLoginToken(this, token.accessToken)

                    getUserInfo()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }

            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("KAKAO_LOGIN", "카카오톡으로 로그인 실패", error)

                        if(error is ClientError && error.reason == ClientErrorCause.Cancelled){
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i("LOGIN", "카카오톡으로 로그인 성공 ${token.accessToken}")

                        getUserInfo()
                    }
                }
            }else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

    }

    private fun getUserInfo() {
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("USER_INFO", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("USER_INFO", "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

                Toast.makeText(this,"회원 번호: ${user.id}, 닉네임: ${user.kakaoAccount?.profile?.nickname}",Toast.LENGTH_SHORT).show()

                // 여기서 가져온 정보를 UI에 그리거나 다음 화면으로 넘겨줄 수 있습니다.
                // 예: binding.nicknameTextView.text = user.kakaoAccount?.profile?.nickname
            }
        }
    }

    //로그인 결과 함수
    private fun observeLoginResult(){
        viewModel.loginResult.observe(this){
            result ->
            result.onSuccess { logindata ->
                //로그인 성공
                Log.d("login_success","로그인 성공")
                Toast.makeText(this,"로그인 성공!", Toast.LENGTH_SHORT).show()

                //뷰모델에 저장된 jwt를 spf에 저장
                val jwt = viewModel.accessToken
                saveJwt(jwt)

                startMainActivity()
                finish()

        }.onFailure { exception ->
            //로그인 실패
            val message = exception.message ?: "알 수 없는 로그인 오류"
                Log.e("login_failure","로그인 실패: $message")
                Toast.makeText(this,"로그인 실패: $message", Toast.LENGTH_SHORT).show()
            }
    }
    }
    private fun login(){
        val id = binding.loginIdEt.text.toString()
        val domain = binding.loginInputEt.text.toString()
        val pwd = binding.loginPasswordEt.text.toString()

        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.loginPasswordEt.text.toString().isEmpty()){
            Toast.makeText(this,"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val email : String = id + "@" + domain

        viewModel.login(email,pwd)
    }

    //access token을 spf에 저장하는 함수
    private fun saveJwt(jwt:String){
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()
        Log.d("SAVE_JWT", "저장할 JWT: $jwt")

        editor.putString("jwt",jwt)
        editor.apply()
        Log.d("SAVE_JWT", "저장된 JWT 최종 값: ${spf.getString("jwt","없음")}")
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}