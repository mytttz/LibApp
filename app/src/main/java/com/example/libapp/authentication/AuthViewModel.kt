package com.example.libapp.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.libapp.account.AccountActivity
import com.example.libapp.database.AppDatabase
import com.example.libapp.database.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    private val authRepository = UserRepository()

    suspend fun login(
        username: String,
        password: String,
        context: Context,
        preferences: SharedPreferences
    ) {
        val isSuccessful = authRepository.login(username, password, context)
        if (isSuccessful) {
            var id: Long?
            CoroutineScope(Dispatchers.IO).launch {
                id = AppDatabase.getDatabase(context).userDao().getUserByUsername(username)?.id
                withContext(Dispatchers.Main) {
                    with(preferences.edit()) {
                        if (id != null) {
                            putLong("user_id", id!!)
                        }
                        apply()
                    }
                    val intent = Intent(context, AccountActivity::class.java)
                    context.startActivity(intent)
                }
            }

        } else {
            Toast.makeText(context, "Wrong login or password!", Toast.LENGTH_SHORT).show()
        }
    }

    fun register(user: User, context: Context, preferences: SharedPreferences) {
//        if (user.password.length < 6) {
//            Toast.makeText(context, "Enter a more complex password!", Toast.LENGTH_SHORT).show()
//        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val userNew = authRepository.register(user, context)
                withContext(Dispatchers.Main) {
                    if (userNew != null) {
                        val id = userNew.id

                        with(preferences.edit()) {
                            putLong("user_id", id)
                            apply()
                        }

                        val intent = Intent(context, AccountActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "The user already exists!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
//}