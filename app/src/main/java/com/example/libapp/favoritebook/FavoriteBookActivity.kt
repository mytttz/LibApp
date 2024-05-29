package com.example.libapp.favoritebook

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.account.AccountActivity
import com.example.libapp.bookcatalog.BookCatalogActivity
import com.example.libapp.bookcatalog.FullDescriptionBookBottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoriteBookActivity : AppCompatActivity() {

    private lateinit var favoriteBookList: RecyclerView
    private lateinit var viewModel: FavoriteBookViewModel
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var userIdPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_book_activity)
        favoriteBookList = findViewById(R.id.favorite_book_list)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.favorite
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.catalog -> {
                    val intent = Intent(this, BookCatalogActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.favorite -> true
                R.id.account -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = userIdPreferences.getLong("user_id", -1L)

        viewModel = FavoriteBookViewModel(this, userId)

        val adapter = FavoriteBookAdapter(this, viewModel, ::showBook)
        favoriteBookList.adapter = adapter
        favoriteBookList.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        viewModel.favoriteBooks.observe(this) { favoriteBooksList ->
            adapter.submitList(favoriteBooksList)
        }

    }
    private fun showBook(
        id: Long
    ) {
        val bottomSheetDialog = FullDescriptionBookBottomSheet()
        val args = Bundle()
        args.putLong("ID", id)
        bottomSheetDialog.arguments = args
        bottomSheetDialog.show(supportFragmentManager, "FullDescriptionBookBottomSheet")
    }
}
