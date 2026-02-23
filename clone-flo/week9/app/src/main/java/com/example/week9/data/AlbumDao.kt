package com.example.week9.data

import androidx.room.*
import com.example.week9.data.Like


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

    @Insert
    fun likeAlbum(like: Like)
    //좋아요를 눌렀는지 안눌렀는지 확인하는 함수
    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLikedAlbum(userId:Int,albumId:Int): Int?
    //좋아요 취소하는 함수
    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun disLikedAlbum(userId:Int,albumId:Int)
    //좋아요한 앨범 조회하는 함수
    //leftjoin - join은 두개의 테이블을 붙여주는것
    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT ON LT.albumId = AT.id WHERE LT.userId = :userId")
    fun getLikedAlbums(userId :Int): List<Album>



}