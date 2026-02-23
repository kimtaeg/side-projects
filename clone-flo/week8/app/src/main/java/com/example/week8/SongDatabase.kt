package com.example.week8

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Song::class,Album::class,User::class,Like::class], version = 3)
abstract class SongDatabase: RoomDatabase()  {
    abstract fun songDao(): SongDao
    //albumdao연결
    abstract fun albumDao(): AlbumDao
    //userdao연결
    abstract fun userDao(): UserDao

    companion object{
        private var instance: SongDatabase? = null

        @Synchronized
        fun getInstance(context: Context): SongDatabase? {
            if(instance==null){
                synchronized(SongDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database"
                    ).allowMainThreadQueries()
                        //스키마 변경 시 데이터베이스를 파괴하고 새로 생성하도록 설정하는거
                        .fallbackToDestructiveMigration()
                        .build() //스레드에 데이터베이스와 통신 allowMainThreadQueries통해서
                }
            }

            return instance
        }
    }
}


