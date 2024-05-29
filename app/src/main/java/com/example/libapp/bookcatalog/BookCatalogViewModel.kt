package com.example.libapp.bookcatalog

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libapp.database.AppDatabase
import com.example.libapp.database.Basket
import com.example.libapp.basket.BasketRepository
import com.example.libapp.database.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BookCatalogViewModel(
    context: Context,
    private val userId: Long
) : ViewModel() {

    private val basketRepository = BasketRepository(AppDatabase.getDatabase(context).basketDao())

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> get() = _books

    private val _typeBooks = MutableLiveData<List<String>>()
    val typeBooks: LiveData<List<String>> get() = _typeBooks

    private val _basket = MutableLiveData<Basket>()
    val basket: LiveData<Basket> get() = _basket

    private val _error = MutableLiveData<String>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _books.postValue(AppDatabase.getDatabase(context).bookDao().getAllBooks())
            _basket.postValue(
                AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)
            )
            val listType = AppDatabase.getDatabase(context).bookDao().getAllBookTypes()
            withContext(Dispatchers.IO) {
                val list = mutableListOf<String>()
                list.add("All")
                list.addAll(listType)
                _typeBooks.postValue(list)
            }
        }
    }

    fun chooseTypeOfBooks(context: Context, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (type == "All") {
                _books.postValue(AppDatabase.getDatabase(context).bookDao().getAllBooks())
            } else {
                _books.postValue(AppDatabase.getDatabase(context).bookDao().getBooksByType(type))
                Log.i("typetype", _books.value?.size.toString())
            }
        }
    }

    fun updateBasket(userId: Long, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            _basket.postValue(
                AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)
            )
        }
    }

    fun addToBasket(context: Context, userId: Long, book: Book) {
        CoroutineScope(Dispatchers.IO).launch {
            basketRepository.manageBookInBasket(userId, book, 1)
            updateBasket(userId, context)
        }
    }

    fun editBook(bookId: Long, context: Context) {
        val intent = Intent(context, CreateOrEditBookActivity::class.java)
        intent.putExtra("ID", bookId)
        intent.putExtra("CreateOrEdit", "Edit")
        context.startActivity(intent)
    }

    fun addToFavorite(book: Book, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteList = AppDatabase.getDatabase(context).favoriteBookListDao().getFavoriteListByUserId(userId)
            favoriteList?.composition?.add(book)
            if (favoriteList != null) {
                AppDatabase.getDatabase(context).favoriteBookListDao().updateFavorite(favoriteList)
            }
        }
    }
    fun removeToFavorite(book: Book, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteList = AppDatabase.getDatabase(context).favoriteBookListDao().getFavoriteListByUserId(userId)
            favoriteList?.composition?.remove(book)
            if (favoriteList != null) {
                AppDatabase.getDatabase(context).favoriteBookListDao().updateFavorite(favoriteList)
            }
        }
    }

}

