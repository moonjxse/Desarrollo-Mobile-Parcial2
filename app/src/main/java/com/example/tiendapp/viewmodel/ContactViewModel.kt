package com.example.tiendapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendapp.data.AppDatabase
import com.example.tiendapp.data.Contact
import com.example.tiendapp.model.ContactRepository
import com.example.tiendapp.model.Region
import com.example.tiendapp.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Formulario de Contacto
 *
 * Maneja la lógica del formulario: validación, estados y guardado en SQLite.
 *
 * Extiende AndroidViewModel porque necesitamos Application context para:
 * - Inicializar Room Database (acceso a SQLite)
 * - Leer regiones.json desde assets
 *
 * @param application Application context
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Repository centraliza acceso a datos (Room y JSON).
     * Separa lógica de presentación (ViewModel) de acceso a datos (Repository).
     */
    private val repository: ContactRepository

    init {
        val database = AppDatabase.getDatabase(application)
        val contactDao = database.contactDao()
        repository = ContactRepository(contactDao, application)
    }

    /**
     * Estados de los 5 campos del formulario.
     * StateFlow permite que la UI se actualice automáticamente al escribir.
     * Patrón: MutableStateFlow privado, StateFlow público (solo lectura para UI).
     */
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()

    private val _regionSeleccionada = MutableStateFlow("")
    val regionSeleccionada: StateFlow<String> = _regionSeleccionada.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    /**
     * Estados de error: null si es válido, String con mensaje si es inválido.
     * La UI muestra estos mensajes debajo de cada campo en tiempo real.
     */
    private val _nombreError = MutableStateFlow<String?>(null)
    val nombreError: StateFlow<String?> = _nombreError.asStateFlow()

    private val _telefonoError = MutableStateFlow<String?>(null)
    val telefonoError: StateFlow<String?> = _telefonoError.asStateFlow()

    private val _correoError = MutableStateFlow<String?>(null)
    val correoError: StateFlow<String?> = _correoError.asStateFlow()

    private val _regionError = MutableStateFlow<String?>(null)
    val regionError: StateFlow<String?> = _regionError.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    /**
     * Regiones de Chile cargadas desde regiones.json de forma asíncrona.
     * Inicia vacía, se llena al crear el ViewModel. Usada por el dropdown.
     */
    private val _regiones = MutableStateFlow<List<Region>>(emptyList())
    val regiones: StateFlow<List<Region>> = _regiones.asStateFlow()

    /**
     * isLoading: muestra spinner mientras guarda en SQLite.
     * guardadoExitoso: muestra Snackbar de éxito y vuelve al Home.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso.asStateFlow()

    /**
     * Cargamos las regiones inmediatamente para que el dropdown las tenga disponibles.
     */
    init {
        loadRegiones()
    }

    /**
     * Carga regiones desde JSON de forma asíncrona usando corrutinas.
     * viewModelScope.launch evita bloquear el hilo principal mientras lee el archivo.
     */
    private fun loadRegiones() {
        viewModelScope.launch {
            _regiones.value = repository.loadRegiones()
        }
    }

    /**
     * Actualiza el nombre y valida en tiempo real.
     * ValidationUtils retorna null si es válido, o mensaje de error si no.
     */
    fun onNombreChange(value: String) {
        _nombre.value = value
        _nombreError.value = ValidationUtils.getNombreErrorMessage(value)
    }

    /**
     * Actualiza el teléfono y valida formato chileno (+56912345678).
     */
    fun onTelefonoChange(value: String) {
        _telefono.value = value
        _telefonoError.value = ValidationUtils.getTelefonoErrorMessage(value)
    }

    /**
     * Actualiza el correo y valida formato email.
     */
    fun onCorreoChange(value: String) {
        _correo.value = value
        _correoError.value = ValidationUtils.getEmailErrorMessage(value)
    }

    /**
     * Actualiza la región seleccionada y valida que no esté vacía.
     */
    fun onRegionChange(value: String) {
        _regionSeleccionada.value = value
        _regionError.value = ValidationUtils.getRegionErrorMessage(value)
    }

    /**
     * Actualiza el mensaje solo si no excede 200 caracteres.
     * Esto previene que el usuario escriba más del límite.
     */
    fun onMensajeChange(value: String) {
        if (value.length <= 200) {
            _mensaje.value = value
            _mensajeError.value = ValidationUtils.getMensajeErrorMessage(value)
        }
    }

    /**
     * Retorna el contador en formato "X/200" para mostrar debajo del campo mensaje.
     */
    fun getMensajeCounter(): String {
        return "${_mensaje.value.length}/200"
    }

    /**
     * Valida todos los campos antes de guardar.
     * Retorna true si todos son válidos (errores = null), false si hay errores.
     */
    private fun validateForm(): Boolean {
        _nombreError.value = ValidationUtils.getNombreErrorMessage(_nombre.value)
        _telefonoError.value = ValidationUtils.getTelefonoErrorMessage(_telefono.value)
        _correoError.value = ValidationUtils.getEmailErrorMessage(_correo.value)
        _regionError.value = ValidationUtils.getRegionErrorMessage(_regionSeleccionada.value)
        _mensajeError.value = ValidationUtils.getMensajeErrorMessage(_mensaje.value)

        return _nombreError.value == null &&
                _telefonoError.value == null &&
                _correoError.value == null &&
                _regionError.value == null &&
                _mensajeError.value == null
    }

    /**
     * Guarda el contacto en SQLite.
     * Valida primero, muestra loading, guarda en Room, limpia formulario y notifica éxito.
     * try-catch-finally asegura que el loading se detenga siempre.
     */
    fun saveContact() {
        if (!validateForm()) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val contact = Contact(
                    id = 0,
                    nombre = _nombre.value.trim(),
                    telefono = _telefono.value.trim(),
                    correo = _correo.value.trim(),
                    region = _regionSeleccionada.value,
                    mensaje = _mensaje.value.trim()
                )

                repository.insertContact(contact)
                _guardadoExitoso.value = true
                clearForm()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia todos los campos y errores del formulario.
     */
    private fun clearForm() {
        _nombre.value = ""
        _telefono.value = ""
        _correo.value = ""
        _regionSeleccionada.value = ""
        _mensaje.value = ""

        _nombreError.value = null
        _telefonoError.value = null
        _correoError.value = null
        _regionError.value = null
        _mensajeError.value = null
    }

    /**
     * Resetea el estado de guardado exitoso después de mostrar el Snackbar.
     */
    fun resetGuardadoExitoso() {
        _guardadoExitoso.value = false
    }
}