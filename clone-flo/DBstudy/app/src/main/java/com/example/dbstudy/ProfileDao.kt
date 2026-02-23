package com.example.dbstudy

import androidx.room.*

//Data access object
@Dao
interface ProfileDao {
    @Insert
    fun insert(profile: Profile)

    @Update
    fun update(profile: Profile)

    @Delete
    fun delete(profile: Profile)

    // list 프로파일에 리턴해라
    @Query("SELECT * FROM Profile")
    fun getAll(): List<Profile>
}