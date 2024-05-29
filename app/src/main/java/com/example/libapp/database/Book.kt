package com.example.libapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Book",
    indices = [Index(value = ["title"], unique = true)]
)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val year: String,
    val authors: String,
    val description: String,
    val type: String,
    var quantity: Int = 1,
    var isFavorite: Boolean = false,
    val image: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (id != other.id) return false
        if (title != other.title) return false
        if (year != other.year) return false
        if (authors != other.authors) return false
        if (description != other.description) return false
        if (type != other.type) return false
        if (quantity != other.quantity) return false
        if (isFavorite != other.isFavorite) return false
        return image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + authors.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + quantity
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}
