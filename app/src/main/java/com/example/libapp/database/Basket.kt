package com.example.libapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libapp.database.Book

@Entity(
    tableName = "Basket",
)
data class Basket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val composition: MutableList<Book> = mutableListOf(),
    val userId: Long
)