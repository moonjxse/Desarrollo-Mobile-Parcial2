package com.example.tiendapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM OrderTable ORDER BY fecha DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM OrderTable WHERE userId = :userId ORDER BY fecha DESC")
    fun getOrdersByUser(userId: Int): Flow<List<Order>>

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("DELETE FROM OrderTable")
    suspend fun deleteAllOrders()

    @Query("SELECT COUNT(*) FROM OrderTable")
    suspend fun getOrderCount(): Int
}
