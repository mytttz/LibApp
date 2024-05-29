package com.example.libapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.libapp.database.Book

@Entity(
    tableName = "Order",
    indices = [Index(value = ["name"], unique = true)]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String?,
    val composition: List<Book>,
    val completeState: Int = 0,
    val userId: Long
)