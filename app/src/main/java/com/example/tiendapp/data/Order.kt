package com.example.tiendapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderTable")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val cantidad: Int,
    val total: Int,
    val estado: String = "Pendiente", // Pendiente, Enviado, Entregado
    val fecha: Long = System.currentTimeMillis()
)

