package com.example.week10.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album (
    @PrimaryKey(autoGenerate = true)var id :Int = 0,//앨범고유 id
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,
    //var songs: ArrayList<Song>? = null
)