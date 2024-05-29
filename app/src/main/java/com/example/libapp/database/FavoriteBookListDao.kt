package com.example.libapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteBookListDao {
    @Query("SELECT * FROM FavoriteBookList WHERE userId = :userId")
    fun getFavoriteListByUserId(userId: Long): FavoriteBookList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favoriteList: FavoriteBookList)

    @Query("DELETE FROM FavoriteBookList WHERE userId = :userId")
    fun deleteFavoriteList(userId: Long)

    @Update
    fun updateFavorite(favoriteList: FavoriteBookList)

}
