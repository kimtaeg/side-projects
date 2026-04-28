package com.example.week8

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM UserTable")
    fun getUser():List<User>

    @Query("SELECT * FROM UserTable WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String) : User?
}