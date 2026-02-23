package com.example.week4

data class Song(
    val title: String = "",
    val singer: String = "",
    //Int 타입이거나, 혹은 값이 없을 수도 있으니 null로 처리해도 괜찮다라는 것
    val coverImg: Int? = null
)