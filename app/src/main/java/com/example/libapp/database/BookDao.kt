package com.example.libapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {

    @Query("SELECT * FROM Book WHERE id = :id")
    fun getBookById(id: Long): Book

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    @Query("DELETE FROM Book WHERE id = :id")
    fun deleteBook(id: Long)

    @Query("SELECT * FROM Book")
    fun getAllBooks(): List<Book>

    @Query("DELETE FROM Book")
    fun deleteAllBooks()

    @Query("SELECT COUNT(*) FROM Book")
    fun getBooksCount(): Int

    @Update
    fun updateBook(book: Book)

    @Query("SELECT * FROM Book WHERE type = :type")
    fun getBooksByType(type: String): List<Book>

    @Query("SELECT DISTINCT type FROM Book")
    fun getAllBookTypes(): List<String>
}