package com.example.libapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "FavoriteBookList",
)
data class FavoriteBookList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val composition: MutableList<Book> = mutableListOf(),
    val userId: Long
)