package com.example.libapp.favoritebook

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libapp.database.AppDatabase
import com.example.libapp.database.Basket
import com.example.libapp.database.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteBookViewModel(
    context: Context,
    private val userId: Long
) : ViewModel() {

    private val _favoriteBooks = MutableLiveData<List<Book>>()
    val favoriteBooks: LiveData<List<Book>> get() = _favoriteBooks


    init {
        CoroutineScope(Dispatchers.IO).launch {
            _favoriteBooks.postValue(AppDatabase.getDatabase(context).favoriteBookListDao().getFavoriteListByUserId(userId)?.composition)
        }
    }

    fun removeFav(context: Context, book: Book) {
        CoroutineScope(Dispatchers.IO).launch {
            var favoriteList = AppDatabase.getDatabase(context).favoriteBookListDao().getFavoriteListByUserId(userId)
            val existingDish = favoriteList?.composition?.find { it.id == book.id }
            favoriteList?.composition?.remove(existingDish)
            if (favoriteList != null) {
                AppDatabase.getDatabase(context).favoriteBookListDao().updateFavorite(favoriteList)
            }
            _favoriteBooks.postValue(AppDatabase.getDatabase(context).favoriteBookListDao().getFavoriteListByUserId(userId)?.composition)
        }
    }
}