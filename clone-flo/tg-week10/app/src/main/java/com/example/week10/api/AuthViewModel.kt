package com.example.week10.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week10.data.LoginData
import com.example.week10.data.SignupData
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    constructor() : this(
        AuthRepository(
            ApiClient.retrofit.create(AuthRetrofitinterface::class.java)
        )
    )
    private val _loginResult = MutableLiveData<Result<LoginData>>()
    val loginResult: LiveData<Result<LoginData>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val request = LoginRequest(email,password)
            val result = repository.Login(request)
            _loginResult.postValue(result)
        }
    }

    private val _signupResult = MutableLiveData<Result<SignupData>>()
    val signupResult: LiveData<Result<SignupData>> = _signupResult

    fun signup(req: SignUpRequest) {
        viewModelScope.launch {
            val result = repository.SignUp(req)
            _signupResult.postValue(result)
        }
    }

    // 로그인 후 토큰 관리
    var Id = 0
    var accessToken =""
    var name: String? = ""
}