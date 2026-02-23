package com.example.week7

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.ArrayList

@Entity(tableName = "AlbumTable")
data class Album (
    @PrimaryKey(autoGenerate = true)var id :Int = 0,//앨범고유 id
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,
    //var songs: ArrayList<Song>? = null
)