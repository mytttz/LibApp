package com.example.libapp.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.authentication.AuthActivity
import com.example.restaurantapp.account.AccountViewModel
import com.example.libapp.bookcatalog.BookCatalogActivity
import com.example.libapp.favoritebook.FavoriteBookActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AccountActivity : AppCompatActivity() {

    private lateinit var completedApplicationsList: RecyclerView
    private lateinit var currentApplicationsList: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var accountName: TextView
    private lateinit var completedApplicationsTitle: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var viewModel: AccountViewModel
    private lateinit var adapterCurrentOrders: CurrentOrderAdapter
    private lateinit var userIdPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_activity)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        completedApplicationsList = findViewById(R.id.completed_applications_list)
        currentApplicationsList = findViewById(R.id.current_applications_list)
        completedApplicationsTitle = findViewById(R.id.completed_applications_title)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        accountName = findViewById(R.id.account_name)
        logoutButton = findViewById(R.id.logout_button)
        val userId = userIdPreferences.getLong("user_id", -1L)
        viewModel = AccountViewModel(this, userId)

        if (userId == -100L) {
            completedApplicationsList.visibility = View.GONE
            completedApplicationsTitle.visibility = View.GONE
        }
        adapterCurrentOrders = CurrentOrderAdapter(::showBook)


        val adapterCompletedOrders = CompletedOrdersAdapter(::showBook)
        completedApplicationsList.layoutManager = LinearLayoutManager(this)
        currentApplicationsList.layoutManager = LinearLayoutManager(this)
        completedApplicationsList.adapter = adapterCompletedOrders
        currentApplicationsList.adapter = adapterCurrentOrders


        viewModel.completedOrders.observe(this) { completedOrders ->
            adapterCompletedOrders.submitList(completedOrders)
        }
        viewModel.currentOrders.observe(this) { currentOrders ->
            adapterCurrentOrders.submitList(currentOrders)
        }
        viewModel.user.observe(this) { user ->
            accountName.text = user.name
        }
        supportFragmentManager.setFragmentResultListener(
            "orderUpdated",
            this,
            FragmentResultListener { requestKey, result ->
                if (requestKey == "orderUpdated") {
                    viewModel.updateOrderList(this)
                }
            })
        bottomNavigation.selectedItemId = R.id.account
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.catalog -> {
                    val intent = Intent(this, BookCatalogActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.favorite -> {
                    val intent = Intent(this, FavoriteBookActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.account -> {
                    true
                }
                else -> false
            }
        }
        logoutButton.setOnClickListener {
            userIdPreferences.edit().clear().apply()
            Log.i("IdUser", userIdPreferences.getLong("user_id", -1L).toString())
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showBook(id: Long) {
        val bottomSheetDialog = FullDescriptionOrderBottomSheet()
        val args = Bundle()
        args.putLong("ID", id)
        bottomSheetDialog.arguments = args
        bottomSheetDialog.show(supportFragmentManager, "FullDescriptionOrderBottomSheet")
    }
}
