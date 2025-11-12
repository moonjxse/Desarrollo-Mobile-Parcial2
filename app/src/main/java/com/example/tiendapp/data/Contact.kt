package com.example.tiendapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity de Room que representa la tabla de contactos en SQLite
 * Cada instancia de esta clase es una fila en la tabla
 *
 * @Entity indica que esta clase es una tabla de Room
 * tableName define el nombre de la tabla en la base de datos
 * "data class" genera automáticamente equals(), hashCode(), toString() y copy()
 * funciones útiles para comparar y manipular objetos de base de datos
 */
@Entity(tableName = "contacts")
data class Contact(
    /**
     * @PrimaryKey indica que este campo es la clave primaria
     * autoGenerate = true hace que Room genere automáticamente un ID único
     * cuando insertamos un nuevo contacto con id = 0
     * Inicializamos con 0 porque Room usa 0 como señal para generar un nuevo ID.
     * Room lo reemplazará automáticamente con el siguiente ID disponible como 1, 2, 3... etc
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Nombre del contacto
     * Este campo se convierte en una columna llamada "nombre" en la tabla
     */
    val nombre: String,

    /**
     * Teléfono del contacto (formato chileno, mas adelante veremos un regex)
     */
    val telefono: String,

    /**
     * Correo electrónico del contacto (mismo formato que regex de la clase pasada)
     */
    val correo: String,

    /**
     * Región seleccionada del contacto
     */
    val region: String,

    /**
     * Mensaje del contacto (máximo 200 caracteres)
     */
    val mensaje: String
)