package com.example.restaurantapp.account

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libapp.database.AppDatabase
import com.example.libapp.database.Order
import com.example.libapp.database.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewModel(
    context: Context,
    userId: Long
) : ViewModel() {

    private val _completedOrders = MutableLiveData<List<Order>>()
    val completedOrders: LiveData<List<Order>> get() = _completedOrders

    private val _currentOrders = MutableLiveData<List<Order>>()
    val currentOrders: LiveData<List<Order>> get() = _currentOrders

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user


    init {
        CoroutineScope(Dispatchers.IO).launch {
            _user.postValue(
                AppDatabase.getDatabase(context).userDao().getUserById(userId)
            )
            if (userId != -100L) {
                _completedOrders.postValue(
                    AppDatabase.getDatabase(context).orderDao().getAllCompleteOrders(userId)
                )
                _currentOrders.postValue(
                    AppDatabase.getDatabase(context).orderDao().getAllCurrentOrders(userId)
                )
            } else {
                _currentOrders.postValue(
                    AppDatabase.getDatabase(context).orderDao().getAllCurrentOrdersForAdmin()
                )
            }
        }
    }


    fun updateOrderList(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentOrders =
                AppDatabase.getDatabase(context).orderDao().getAllCurrentOrdersForAdmin()
            withContext(Dispatchers.Main) {
                _currentOrders.value = currentOrders
            }
        }
    }
}
