package com.example.dbstudy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 버전은 테이블개수가 늘어나거나 형식이 바뀌면 비전도 바꿔야한다.
@Database(entities = [Profile::class], version = 1)
abstract class ProfileDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    /*내부 인스턴스는 객체를 여러개를 만들어도 한번만 생성하게 만든다 왜냐하면 db는 하나이고 데이터를 crud하면 되는거고
    db가 여러개면 메모리만 낭비*/
    companion object{

        private var instance: ProfileDatabase? = null

        // Synchronized 동기화 두개의 스레드가 동시에 접근할 수 없게 해준다.
        @Synchronized
        fun getInstance(context: Context): ProfileDatabase? {
            if (instance == null) {
                synchronized(ProfileDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProfileDatabase::class.java,
                        "user-database"//다른 데이터 배이스랑 이름겹치면 꼬임
                    ).build()
                }
            }
            return instance
        }
    }
}