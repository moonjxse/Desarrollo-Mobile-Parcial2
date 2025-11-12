package com.example.tiendapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val imagenUrl: String? = null,
    val stock: Int = 0
)
