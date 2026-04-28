package com.example.week9.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    val title: String = "",
    val singer: String = "",
    //Int 타입이거나, 혹은 값이 없을 수도 있으니 null로 처리해도 괜찮다라는 것
    var second: Int = 0,
    val playTime: Int = 0,
    // 재생 되고 있는지
    var isPlaying: Boolean = false,
    // 어떤 음악이 재생되는지 알려주는 변수
    var music : String = "",
    val coverImg: Int? = null,
    var isLike: Boolean = false,
    //song이 속한 album의 id
    var albumIdx: Int = 0
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

