package com.example.tiendapp.model

import com.example.tiendapp.data.Order
import com.example.tiendapp.data.OrderDao
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {

    fun getOrdersByUser(userId: Int): Flow<List<Order>> {
        return orderDao.getOrdersByUser(userId)
    }

    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteOrder(order: Order) {
        orderDao.deleteOrder(order)
    }

    suspend fun clearAllOrders() {
        orderDao.deleteAllOrders()
    }
}
