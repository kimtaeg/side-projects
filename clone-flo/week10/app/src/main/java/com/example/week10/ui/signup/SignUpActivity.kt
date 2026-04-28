package com.example.week10.ui.signup

import com.example.week10.ui.main.MainActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.week10.api.AuthViewModel
import com.example.week10.api.SignUpRequest
import com.example.week10.data.User
import com.example.week10.databinding.ActivitySignupBinding

class SignUpActivity: AppCompatActivity() {

    lateinit var binding : ActivitySignupBinding

    //viewmodel 선언
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // livedata 관찰
        observeSignupResult()

        // 가입완료버튼을 눌렀을때 사인업 실행
        binding.signupSignupBtn.setOnClickListener {
                signUp()
            }
        }



    private fun observeSignupResult(){
        authViewModel.signupResult.observe(this){result ->
            result.onSuccess { data ->
                Log.d("SignUpActivity", "observeSignup: onSuccess, memberId=${data.Id}, nickname=${data.name}")
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                binding.signupStatusTv.visibility = View.GONE
                authViewModel.Id = data.Id
                authViewModel.name = data.name
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure { error ->
                binding.signupStatusTv.visibility = View.VISIBLE
                binding.signupStatusTv.text = "이미 회원가입된 유저입니다."
            }
        }
    }


    // 사용자의 입력한 값을 가져오는 함수
    private fun getUser() : User {
        val email : String = binding.signupIdEt.text.toString() + "@" + binding.signupInputEt.text.toString()
        val pwd : String = binding.signupPasswordEt.text.toString()
        val name : String = binding.signupNicknameEt.text.toString()


        return User(email, pwd, name)
    }


    private fun signUp(){
        val email : String = binding.signupIdEt.text.toString() + "@" + binding.signupInputEt.text.toString()
        val pwd : String = binding.signupPasswordEt.text.toString()
        val name : String = binding.signupNicknameEt.text.toString()

        //입력하지않은 경우 진행x
        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.signupPasswordEt.text.toString() != binding.signupPasswordCheckEt.text.toString()){
            binding.signupPasswordTv.visibility = View.VISIBLE
            binding.signupPasswordTv.text = "비밀번호가 일치하지 않습니다."
            return
        }
        if(binding.signupPasswordEt.text.toString().isEmpty() || binding.signupPasswordCheckEt.text.toString().isEmpty()) {
            Toast.makeText(this,"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.signupNicknameEt.text.toString().isEmpty()){
            Toast.makeText(this,"닉네임 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // 이메일 형식 유효성 검사
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "유효한 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호 길이 검사 (서버 유효성 검사 실패 가능성)
        if (binding.signupPasswordEt.text.toString().length < 4) { // 예시: 최소 8자
            Toast.makeText(this, "비밀번호는 4자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val req = SignUpRequest(email = email, password = pwd, name = name)
        //뷰모델 통해 api 호출
        authViewModel.signup(req)
    }
}