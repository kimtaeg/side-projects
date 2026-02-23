package com.example.week6

data class Song(
    val title: String = "",
    val singer: String = "",
    //Int 타입이거나, 혹은 값이 없을 수도 있으니 null로 처리해도 괜찮다라는 것
    var second: Int = 0,
    val playTime: Int = 0,
    var isPlaying: Boolean = false,
    // 어떤 음악이 재생되는지 알려주는 변수
    var music : String = "",
    val coverImg: Int? = null
)