package com.example.tiendapp.utils

/**
 * Object que contiene funciones de validación para el formulario
 *
 * object crea un Singleton automáticamente
 * Esto permite usar las funciones sin crear instancias:
 * ValidationUtils.isValidEmail("test@test.com")
 */
object ValidationUtils {

    /**
     * Valida que el nombre solo contenga letras y espacios
     * Mínimo 2 caracteres, máximo 50
     *
     * @param nombre Nombre a validar
     * @return true si el nombre es válido, false si no
     */
    fun isValidNombre(nombre: String): Boolean {
        // Verificar que no esté vacío
        if (nombre.isBlank()) return false

        // Verificar longitud
        if (nombre.length < 2 || nombre.length > 50) return false

        /**
         * Regex para validar nombre:
         * ^              = Inicio de la cadena
         * [a-zA-ZáéíóúÁÉÍÓÚñÑ]  = Una letra (incluye acentos y ñ)
         * [a-zA-ZáéíóúÁÉÍÓÚñÑ ]*  = Cero o más letras o espacios
         * $              = Fin de la cadena
         *
         * Esto permite: "Juan", "María José", "José Luis"
         * No permite: "Juan123", "Juan@", "123"
         */
        val nombreRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ][a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$".toRegex()
        return nombreRegex.matches(nombre)
    }

    /**
     * Valida que el teléfono tenga formato chileno
     * Formatos aceptados:
     * - +56912345678
     * - 56912345678
     * - 912345678
     *
     * @param telefono Teléfono a validar
     * @return true si el teléfono es válido, false si no
     */
    fun isValidTelefono(telefono: String): Boolean {
        if (telefono.isBlank()) return false

        /**
         * Regex para validar teléfono chileno:
         * ^              = Inicio
         * (\\+56|56)?    = Opcional: +56 o 56 (código de país)
         * 9              = Obligatorio: 9 (celulares en Chile empiezan con 9)
         * [0-9]{8}       = Exactamente 8 dígitos más
         * $              = Fin
         *
         * Ejemplos válidos:
         * +56912345678 (9 dígitos con código país)
         * 56912345678
         * 912345678 (9 dígitos sin código)
         */
        val telefonoRegex = "^(\\+56|56)?9[0-9]{8}$".toRegex()
        return telefonoRegex.matches(telefono)
    }

    /**
     * Valida que el email tenga un formato correcto
     * @param email Email a validar
     * @return true si el email es válido, false si no
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        /**
         * Regex para validar email:
         * ^                      = Inicio de la cadena
         * [A-Za-z0-9+_.-]+       = Uno o más caracteres válidos antes del @
         * @                      = Arroba literal
         * [A-Za-z0-9.-]+         = Uno o más caracteres para el dominio
         * \\.                    = Punto literal (escapado)
         * [A-Za-z]{2,}           = Al menos 2 letras para la extensión
         * $                      = Fin de la cadena
         *
         * Ejemplos válidos: usuario@ejemplo.com, test@test.cl
         * Ejemplos inválidos: usuario, usuario@, @ejemplo.com
         */
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Valida que la región no esté vacía
     * @param region Región a validar
     * @return true si la región es válida, false si no
     */
    fun isValidRegion(region: String): Boolean {
        return region.isNotBlank()
    }

    /**
     * Valida que el mensaje no esté vacío y no exceda 200 caracteres
     * @param mensaje Mensaje a validar
     * @return true si el mensaje es válido, false si no
     */
    fun isValidMensaje(mensaje: String): Boolean {
        return mensaje.isNotBlank() && mensaje.length <= 200
    }

    /**
     * Obtiene el mensaje de error para el campo nombre
     * @param nombre Nombre a validar
     * @return Mensaje de error o null si es válido
     */
    fun getNombreErrorMessage(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es requerido"
            nombre.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            nombre.length > 50 -> "El nombre no puede exceder 50 caracteres"
            !isValidNombre(nombre) -> "El nombre solo puede contener letras y espacios"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo teléfono
     * @param telefono Teléfono a validar
     * @return Mensaje de error o null si es válido
     */
    fun getTelefonoErrorMessage(telefono: String): String? {
        return when {
            telefono.isBlank() -> "El teléfono es requerido"
            !isValidTelefono(telefono) -> "Formato inválido. Use: +56912345678 o 912345678"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo email
     * @param email Email a validar
     * @return Mensaje de error o null si es válido
     */
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "El correo electrónico es requerido"
            !isValidEmail(email) -> "Correo electrónico inválido"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo región
     * @param region Región a validar
     * @return Mensaje de error o null si es válida
     */
    fun getRegionErrorMessage(region: String): String? {
        return when {
            region.isBlank() -> "Debe seleccionar una región"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo mensaje
     * @param mensaje Mensaje a validar
     * @return Mensaje de error o null si es válido
     */
    fun getMensajeErrorMessage(mensaje: String): String? {
        return when {
            mensaje.isBlank() -> "El mensaje es requerido"
            mensaje.length > 200 -> "El mensaje no puede exceder 200 caracteres"
            else -> null
        }
    }
}