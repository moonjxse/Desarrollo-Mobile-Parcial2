package com.example.tiendapp.utils

/**
 * Utilidad para manejar roles de usuario dentro de la aplicación.
 *
 * Sirve para distinguir si el usuario tiene privilegios de administrador o no.
 */
object RoleUtils {

    /**
     * Verifica si el rol corresponde a un administrador.
     * @param isAdmin Boolean que indica si el usuario es administrador.
     * @return String con el rol correspondiente.
     */
    fun getRole(isAdmin: Boolean): String {
        return if (isAdmin) "Administrador" else "Cliente"
    }

    /**
     * Retorna el mensaje de bienvenida según el tipo de usuario.
     */
    fun getWelcomeMessage(isAdmin: Boolean, name: String): String {
        return if (isAdmin) {
            "Bienvenido, administrador $name "
        } else {
            "Hola $name, ¡bienvenido a TiendApp! "
        }
    }
}
