package com.example.libapp.bookcatalog

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.account.AccountActivity
import com.example.libapp.basket.BasketActivity
import com.example.libapp.favoritebook.FavoriteBookActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BookCatalogActivity : AppCompatActivity() {

    private lateinit var catalogList: RecyclerView
    private lateinit var typeBooksRecycler: RecyclerView
    private lateinit var viewModel: BookCatalogViewModel
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var basketButton: MaterialButton
    private lateinit var addButton: FloatingActionButton
    private lateinit var userIdPreferences: SharedPreferences
    private var userId = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_list_activity)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = userIdPreferences.getLong("user_id", -1L)
        catalogList = findViewById(R.id.menu_list)
        typeBooksRecycler = findViewById(R.id.type_dishes_list)
        basketButton = findViewById(R.id.basket_button)
        addButton = findViewById(R.id.add_button)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        Log.i("asdasdasd", userId.toString())
        viewModel = BookCatalogViewModel(this, userId)
        val adapterMenu = BookAdapter(userId, this, viewModel, ::showBook)
        val adapterType = TypeBookAdapter(this, viewModel)
        if (userId == -100L) {
            addButton.visibility = View.VISIBLE
        }
        catalogList.adapter = adapterMenu
        typeBooksRecycler.adapter = adapterType
        catalogList.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        typeBooksRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bottomNavigation.selectedItemId = R.id.catalog
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.catalog -> {
                    true
                }
                R.id.favorite -> {
                    val intent = Intent(this, FavoriteBookActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.account -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        basketButton.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        addButton.setOnClickListener {
            val intent = Intent(this, CreateOrEditBookActivity::class.java)
            intent.putExtra("CreateOrEdit", "Create")
            startActivity(intent)

        }
        viewModel.books.observe(this) { listBooks ->
            adapterMenu.submitList(listBooks)
        }

        viewModel.typeBooks.observe(this) { typeBooks ->
            adapterType.submitList(typeBooks)
        }
        viewModel.basket.observe(this) { basket ->
            if (userId != -100L) {
                if (basket.composition.isEmpty()) {
                    basketButton.visibility = View.GONE
                } else {
                    basketButton.visibility = View.VISIBLE
                }
            }
        }
        supportFragmentManager.setFragmentResultListener(
            "bookAdd",
            this,
            FragmentResultListener { requestKey, result ->
                if (requestKey == "bookAdd") {
                    viewModel.updateBasket(userId, this)
                }
            })

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

    override fun onResume() {
        super.onResume()
        viewModel.updateBasket(userId, this)
    }
}