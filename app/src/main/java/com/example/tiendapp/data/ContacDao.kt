package com.example.tiendapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) define las operaciones de acceso a la base de datos
 * @Dao indica que esta interface contiene métodos de acceso a datos
 * Room implementará automáticamente esta interface en tiempo de compilación
 * Doc: https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
interface ContactDao {

    /**
     * Inserta un nuevo contacto en la base de datos
     * @Insert genera automáticamente el query INSERT
     *
     * onConflict = OnConflictStrategy.REPLACE significa que si ya existe
     * un contacto con el mismo ID, lo reemplazará en lugar de generar error
     *
     * suspend indica que esta función es asíncrona y debe ejecutarse
     * en un contexto de corrutina (no bloquea el hilo principal)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    /**
     * Obtiene todos los contactos de la base de datos
     * @Query permite escribir queries SQL personalizadas
     *
     * Flow<List<Contact>> es un stream reactivo que emite automáticamente
     * la lista actualizada cada vez que la tabla cambia
     *
     * No necesita suspend porque Flow ya es asíncrono por naturaleza
     */
    @Query("SELECT * FROM contacts ORDER BY id DESC")
    fun getAllContacts(): Flow<List<Contact>>

    /**
     * Obtiene un contacto específico por su ID
     * :id es un parámetro que se reemplaza con el valor que pasemos
     */
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): Contact?

    /**
     * Elimina un contacto de la base de datos
     * @Delete genera automáticamente el query DELETE
     * Room identifica el registro por su Primary Key (id)
     */
    @Delete
    suspend fun deleteContact(contact: Contact)

    /**
     * Elimina todos los contactos de la base de datos
     * Útil para testing o resetear datos
     */
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    /**
     * Obtiene el número total de contactos guardados
     */
    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int
}