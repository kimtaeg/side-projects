package com.example.week8

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSongs(id: Int): Song

    //좋아요
    @Query("UPDATE SongTable SET isLike= :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int)

    @Query("SELECT * FROM SongTable WHERE isLike= :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

    //좋아요를 한번에 없애는 쿼리문
    @Query("UPDATE SongTable SET isLike = :isLike WHERE isLike = true")
    fun updateAllLikedSongs(isLike: Boolean)

    // 앨범 ID를 기반으로 해당 앨범의 모든 노래를 가져오는 쿼리
    @Query("SELECT * FROM SongTable WHERE albumIdx = :albumId")
    fun getSongsByAlbumId(albumId: Int): List<Song>

}