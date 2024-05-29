package com.example.libapp.basket


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.bookcatalog.BookCatalogActivity
import com.example.libapp.bookcatalog.FullDescriptionBookBottomSheet
import com.example.libapp.database.User
import com.example.libapp.database.Order
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BasketActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var userNameLayout: TextInputLayout
    private lateinit var userNameEditText: TextInputEditText
    private lateinit var userSurnameLayout: TextInputLayout
    private lateinit var userSurnameEditText: TextInputEditText
    private lateinit var userPatronymicLayout: TextInputLayout
    private lateinit var userPatronymicEditText: TextInputEditText
    private lateinit var userPhoneLayout: TextInputLayout
    private lateinit var userPhoneEditText: TextInputEditText
    private lateinit var booksListRecyclerView: RecyclerView
    private lateinit var saveOrderButton: MaterialButton
    private lateinit var viewModel: BasketViewModel
    private lateinit var adapter: BasketAdapter
    private lateinit var preferences: SharedPreferences
    private lateinit var userIdPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basket_activity)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = userIdPreferences.getLong("user_id", -1L)
        toolbar = findViewById(R.id.topAppBar)
        userNameLayout = findViewById(R.id.user_name)
        userNameEditText = findViewById(R.id.user_name_text)
        userSurnameLayout = findViewById(R.id.user_surname)
        userSurnameEditText = findViewById(R.id.user_surname_text)
        userPatronymicLayout = findViewById(R.id.user_patronymic)
        userPatronymicEditText = findViewById(R.id.user_patronymic_text)
        userPhoneLayout = findViewById(R.id.user_phone)
        userPhoneEditText = findViewById(R.id.user_phone_text)
        booksListRecyclerView = findViewById(R.id.dish_list)
        saveOrderButton = findViewById(R.id.save_order_button)

        viewModel = BasketViewModel(this, userId)
        adapter = BasketAdapter(userId, this, viewModel, ::showBook)
        booksListRecyclerView.adapter = adapter
        booksListRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.basket.observe(this) { basket ->
            adapter.submitList(basket)
        }

        viewModel.user.observe(this) { user ->
            userNameEditText.setText(user.name)
            userSurnameEditText.setText(user.surname)
            userPatronymicEditText.setText(user.patronymic)
            userPhoneEditText.setText(user.phone)
        }

        saveOrderButton.setOnClickListener {
            if (isFieldsNotEmpty()) {
                val username = viewModel.user.value?.username
                val password = viewModel.user.value?.password
                val user = User(
                    id = userId,
                    username = username.toString(),
                    password = password.toString(),
                    phone = userPhoneEditText.text.toString(),
                    name =userNameEditText.text.toString(),
                    surname = userSurnameEditText.text.toString(),
                    patronymic = userPatronymicEditText.text.toString(),
                )
                val order = viewModel.basket.value?.let { it1 ->
                    Order(
                        name = "Order from ${getCurrentTimeFormatted()}",
                        composition = it1,
                        userId = userId
                    )
                }
                if (order != null) {
                    viewModel.createOrder(this, order, user)
                }
            } else {
                Toast.makeText(this, "Заполните поля!", Toast.LENGTH_SHORT).show()
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    viewModel.deleteBasket(userId, this)
                    val intent = Intent(this, BookCatalogActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
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

    private fun isFieldsNotEmpty(): Boolean {
        return userNameEditText.text?.isNotBlank() == true && userSurnameEditText.text?.isNotBlank() == true &&
                userPatronymicEditText.text?.isNotBlank() == true && userPhoneEditText.text?.isNotBlank() == true
    }

    private fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.US)
        val date = Date()
        return dateFormat.format(date)
    }
}