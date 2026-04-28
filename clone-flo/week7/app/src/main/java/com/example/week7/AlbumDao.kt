package com.example.week7

import androidx.room.*


@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)
    @Delete
    fun delete(album: Album)
    //모든 앨범 조회
    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album
}