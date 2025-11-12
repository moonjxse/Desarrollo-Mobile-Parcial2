package com.example.tiendapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // 🔹 NUEVA función: insertar lista completa de productos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM Product ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM Product WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Query("DELETE FROM Product")
    suspend fun deleteAllProducts()

    // 🔹 Renombrado para consistencia con el ViewModel
    @Query("SELECT COUNT(*) FROM Product")
    suspend fun countProducts(): Int
}
