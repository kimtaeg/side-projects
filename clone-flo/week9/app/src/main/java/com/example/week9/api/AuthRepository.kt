package com.example.week9.api

import android.util.Log
import com.example.week9.data.LoginData
import com.example.week9.data.SignupData

class AuthRepository (private val service: AuthRetrofitinterface) {

    //로그인
    suspend fun Login(req: LoginRequest): Result<LoginData> = try{
        val response = service.login(req)

        //성공 리턴
        if(response.isSuccessful){
            val body = response.body()

            if(body == null){
                Log.d("tag","Response body is null")
                Result.failure(RuntimeException("Response body is null"))
            } //data값이 없을때
            else if (body.data == null){
                Log.d("tag","Response Ok but Data is null")
                Result.failure(RuntimeException("Response Ok but Data is null"))
            }
            else{
                Log.d("tag","ok")
                Result.success(body.data)
            }
        }
        //잘못 리턴
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("tag", "비상: $errMsg")
            Result.failure(RuntimeException("http ${response.code()}: $errMsg"))
        }
    } catch (e:Exception){
        //오류
        Result.failure(e)
    }

    suspend fun SignUp(req: SignUpRequest): Result<SignupData> = try{
        val response = service.signup(req)

        //성공 리턴
        if(response.isSuccessful){
            val body = response.body()

            if(body == null){
                Log.d("tag","Response body is null")
                Result.failure(RuntimeException("Response body is null"))
            } //data값이 없을때
            else if (body.data == null){
                Log.d("tag","Response Ok but Data is null")
                Result.failure(RuntimeException("Response Ok but Data is null"))
            }
            else{
                Log.d("tag","ok")
                Result.success(body.data)
            }
        }
        //잘못 리턴
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("tag", "비상: $errMsg")
            Result.failure(RuntimeException("http ${response.code()}: $errMsg"))
        }
    } catch (e:Exception){
        //오류
        Result.failure(e)
    }
}