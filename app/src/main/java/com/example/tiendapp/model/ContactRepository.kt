package com.example.tiendapp.model

import android.content.Context
import com.example.tiendapp.data.Contact
import com.example.tiendapp.data.ContactDao
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Repository que maneja la lógica de negocio de contactos
 *
 * En MVVM, el Repository es el intermediario entre el ViewModel y las fuentes de datos
 * Puede combinar datos de múltiples fuentes: Room, API REST, archivos locales, etc.
 *
 * @param contactDao DAO para acceder a la base de datos de contactos
 * @param context Contexto de Android para acceder a assets
 */
class ContactRepository(
    // pasamos por parametro contactDao para operaciones CRUD en SQLite
    private val contactDao: ContactDao,

    // Necesario para leer regiones.json desde assets
    private val context: Context
) {

    /**
     * Flow es un stream reactivo que emite valores automáticamente cuando cambian
     * Este Flow emite la lista de contactos cada vez que se inserta, actualiza o elimina
     * un contacto en la base de datos. El ViewModel puede observar estos cambios y
     * actualizar la UI automáticamente sin necesidad de refrescar manualmente.
     */
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    /**
     * Inserta un nuevo contacto en la base de datos
     * suspend function porque es una operación asíncrona de base de datos
     *
     * @param contact Contacto a insertar
     */
    suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    /**
     * Obtiene un contacto específico por su ID
     * o tambien devuelve null, ya que es nullable
     * @param id ID del contacto
     * @return Contacto encontrado o null si no existe
     */
    suspend fun getContactById(id: Int): Contact? {
        return contactDao.getContactById(id)
    }

    /**
     * Elimina un contacto de la base de datos, pide como argumento el contacto a eliminar
     * @param contact Contacto a eliminar
     */
    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    /**
     * Elimina todos los contactos de la base de datos
     */
    suspend fun deleteAllContacts() {
        contactDao.deleteAllContacts()
    }

    /**
     * Obtiene el número total de contactos guardados, nos devuelve un int como cantidad
     * @return Cantidad de contactos
     */
    suspend fun getContactCount(): Int {
        return contactDao.getContactCount()
    }

    /**
     * Carga las regiones de Chile desde el archivo JSON en assets
     *
     * Esta función lee el archivo regiones.json, lo parsea y retorna
     * una lista de objetos Region
     *
     * @return Lista de regiones o lista vacía si hay error
     */
    fun loadRegiones(): List<Region> {
        return try {
            // Abrir el archivo desde assets
            val jsonString = context.assets.open("regiones.json")
                .bufferedReader()
                .use { it.readText() }

            // Parsear el JSON a lista de Region
            // Json.decodeFromString es type-safe gracias a @Serializable
            Json.decodeFromString<List<Region>>(jsonString)

        } catch (e: IOException) {
            // Si hay error al leer el archivo, retornamos lista vacía
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            // Si hay error al parsear JSON, retornamos lista vacía
            e.printStackTrace()
            emptyList()
        }
    }
}