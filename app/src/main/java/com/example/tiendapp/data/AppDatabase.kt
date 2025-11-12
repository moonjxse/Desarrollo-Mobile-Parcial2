package com.example.tiendapp.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase principal de la base de datos Room
 *
 * @Database indica que esta clase es una base de datos Room
 * entities = listado de todas las entidades (tablas) de la base de datos
 * version = número de versión del esquema (incrementar al cambiar estructura)
 * exportSchema = false evita exportar el esquema a archivos JSON
 */
@Database(
    entities = [
        Contact::class,
        User::class,
        Product::class,
        Order::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Función abstracta que provee acceso al DAO de contactos
     * Room implementará esta función automáticamente
     */
    abstract fun contactDao(): ContactDao

    /**
     * Companion object para implementar el patrón Singleton
     * Esto asegura que solo exista una instancia de la base de datos
     * en toda la aplicación, evitando múltiples conexiones
     */

    abstract fun userDao(): UserDao

    abstract fun productDao(): ProductDao

    abstract fun orderDao(): OrderDao

    /**
     * Companion object para implementar el patrón Singleton
     * Esto asegura que solo exista una instancia de la base de datos
     * en toda la aplicación, evitando múltiples conexiones
     */
    companion object {
        /**
         * @Volatile garantiza que los cambios en INSTANCE sean visibles
         * inmediatamente para todos los threads (hilos)
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos
         * Si no existe, la crea. Si ya existe, la retorna
         *
         * @param context Contexto de Android necesario para crear la DB
         * @return Instancia única de AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE no es null, retornamos la instancia existente
            return INSTANCE ?: synchronized(this) {
                // synchronized evita que múltiples threads creen la DB simultáneamente

                // Construimos la base de datos con Room.databaseBuilder
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tiendapp_database"  // Nombre del archivo SQLite
                )

                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}