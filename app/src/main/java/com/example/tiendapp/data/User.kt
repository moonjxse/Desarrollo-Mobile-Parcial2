package com.example.tiendapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val isAdmin: Boolean = false  // true = administrador, false = cliente
)
