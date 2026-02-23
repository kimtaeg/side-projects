package com.example.week8

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week8.databinding.ActivitySignupBinding

class SignUpActivity: AppCompatActivity() {

    lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 가입완료버튼을 눌렀을때 사인업 실행
        binding.signupSignupBtn.setOnClickListener {
            if(signUp()){
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 사용자의 입력한 값을 가져오는 함수
    private fun getUser() : User{
        val email : String = binding.signupIdEt.text.toString() + "@" + binding.signupInputEt.text.toString()
        val pwd : String = binding.signupPasswordEt.text.toString()

        return User(email, pwd)
    }

    //회원가입 함수
    private fun signUp() : Boolean{
        //입력하지않은 경우 진행x
        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일 형식이 잘못되었습니다.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.signupPasswordEt.text.toString() != binding.signupPasswordCheckEt.text.toString()){
            Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.signupPasswordEt.text.toString().isEmpty() || binding.signupPasswordCheckEt.text.toString().isEmpty()) {
            Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            return false
        }
        //db에 저장
        val userDB = SongDatabase.getInstance(this)!!
        userDB.userDao().insert(getUser())

        val user = userDB.userDao().getUser()
        Log.d("SIGUPACT", user.toString())

        return true
    }

}
