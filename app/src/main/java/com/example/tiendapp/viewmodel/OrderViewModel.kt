package com.example.tiendapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendapp.data.AppDatabase
import com.example.tiendapp.data.Order
import com.example.tiendapp.model.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderRepository

    init {
        val orderDao = AppDatabase.getDatabase(application).orderDao()
        repository = OrderRepository(orderDao)
    }

    val allOrders = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _userOrders = MutableStateFlow<List<Order>>(emptyList())
    val userOrders: StateFlow<List<Order>> = _userOrders.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadOrdersByUser(userId: Int) {
        viewModelScope.launch {
            repository.getOrdersByUser(userId).collect { orders ->
                _userOrders.value = orders
            }
        }
    }

    fun insertOrder(order: Order) {
        viewModelScope.launch {
            repository.insertOrder(order)
            _message.value = "Pedido registrado correctamente"
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order)
            _message.value = "Pedido actualizado correctamente"
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.deleteOrder(order)
            _message.value = "Pedido eliminado correctamente"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
