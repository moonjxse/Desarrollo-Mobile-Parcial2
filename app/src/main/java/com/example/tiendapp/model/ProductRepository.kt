package com.example.tiendapp.model

import com.example.tiendapp.data.Product
import com.example.tiendapp.data.ProductDao
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)
    }
    suspend fun clearAllProducts() {
        productDao.deleteAllProducts()
    }

    suspend fun insertAll(products: List<Product>) {
        productDao.insertAll(products)
    }

    suspend fun countProducts(): Int {
        return productDao.countProducts()
    }
}

