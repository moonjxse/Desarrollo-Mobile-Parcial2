package com.example.tiendapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tiendapp.viewmodel.ContactViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Formulario de contacto con 5 campos, validaciones en tiempo real, dropdown regiones y botón con loading.
 *
 * @param contactViewModel ViewModel con la lógica del formulario
 * @param onNavigateBack Callback para volver al Home
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(
    contactViewModel: ContactViewModel,
    onNavigateBack: () -> Unit
) {
    /**
     * Observar estados del ViewModel. collectAsStateWithLifecycle convierte StateFlow en State lifecycle-aware (detiene en background).
     */
    val nombre by contactViewModel.nombre.collectAsStateWithLifecycle()
    val telefono by contactViewModel.telefono.collectAsStateWithLifecycle()
    val correo by contactViewModel.correo.collectAsStateWithLifecycle()
    val regionSeleccionada by contactViewModel.regionSeleccionada.collectAsStateWithLifecycle()
    val mensaje by contactViewModel.mensaje.collectAsStateWithLifecycle()

    val nombreError by contactViewModel.nombreError.collectAsStateWithLifecycle()
    val telefonoError by contactViewModel.telefonoError.collectAsStateWithLifecycle()
    val correoError by contactViewModel.correoError.collectAsStateWithLifecycle()
    val regionError by contactViewModel.regionError.collectAsStateWithLifecycle()
    val mensajeError by contactViewModel.mensajeError.collectAsStateWithLifecycle()

    val regiones by contactViewModel.regiones.collectAsStateWithLifecycle()
    val isLoading by contactViewModel.isLoading.collectAsStateWithLifecycle()
    val guardadoExitoso by contactViewModel.guardadoExitoso.collectAsStateWithLifecycle()

    /**
     * SnackbarHostState muestra mensajes tipo toast.
     */
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * Scope para corrutinas.
     */
    val scope = rememberCoroutineScope()

    /**
     * Muestra Snackbar al guardar exitosamente, resetea flag y vuelve al Home.
     */
    LaunchedEffect(guardadoExitoso) {
        if (guardadoExitoso) {
            snackbarHostState.showSnackbar(
                message = "Contacto guardado exitosamente",
                duration = SnackbarDuration.Short
            )
            contactViewModel.resetGuardadoExitoso()
            kotlinx.coroutines.delay(1000)  // Esperar 1 segundo
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulario de Contacto") },
                navigationIcon = {
                    /**
                     * Botón back en navbar.
                     */
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        /**
         * Column con scroll vertical para el formulario.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /**
             * Título del formulario.
             */
            Text(
                text = "Complete sus datos",
                style = MaterialTheme.typography.headlineSmall
            )

            /**
             * Campo Nombre.
             */
            OutlinedTextField(
                value = nombre,
                onValueChange = { contactViewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                placeholder = { Text("Ingrese su nombre completo") },
                isError = nombreError != null,
                supportingText = {
                    nombreError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            /**
             * Campo Teléfono.
             */
            OutlinedTextField(
                value = telefono,
                onValueChange = { contactViewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                placeholder = { Text("+56912345678") },
                isError = telefonoError != null,
                supportingText = {
                    telefonoError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            /**
             * Campo Email.
             */
            OutlinedTextField(
                value = correo,
                onValueChange = { contactViewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                isError = correoError != null,
                supportingText = {
                    correoError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            /**
             * Dropdown de Regiones.
             */
            RegionDropdown(
                regiones = regiones,
                selectedRegion = regionSeleccionada,
                onRegionSelected = { contactViewModel.onRegionChange(it) },
                error = regionError
            )

            /**
             * Campo Mensaje (TextArea con contador).
             */
            OutlinedTextField(
                value = mensaje,
                onValueChange = { contactViewModel.onMensajeChange(it) },
                label = { Text("Mensaje") },
                placeholder = { Text("Escriba su mensaje aquí...") },
                isError = mensajeError != null,
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        /**
                         * Mensaje de error o vacío.
                         */
                        Text(
                            text = mensajeError ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                        /**
                         * Contador de caracteres.
                         */
                        Text(
                            text = contactViewModel.getMensajeCounter(),
                            color = if (mensaje.length > 180)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(8.dp))

            /**
             * Botón Guardar con loading.
             */
            Button(
                onClick = { contactViewModel.saveContact() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    /**
                     * Loading indicator.
                     */
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Contacto", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

/**
 * Dropdown de regiones con ExposedDropdownMenuBox. Regiones cargadas desde JSON.
 *
 * @param regiones Lista de regiones disponibles
 * @param selectedRegion Región seleccionada
 * @param onRegionSelected Callback al seleccionar
 * @param error Mensaje de error o null
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDropdown(
    regiones: List<com.example.tiendapp.model.Region>,
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    error: String?
) {
    /**
     * Estado controla si dropdown está expandido.
     */
    var expanded by remember { mutableStateOf(false) }

    /**
     * ExposedDropdownMenuBox crea TextField con menú desplegable.
     */
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        /**
         * TextField muestra región seleccionada. menuAnchor conecta con dropdown. Read-only.
         */
        OutlinedTextField(
            value = selectedRegion,
            onValueChange = {},  // Read-only
            readOnly = true,
            label = { Text("Región") },
            placeholder = { Text("Seleccione una región") },
            trailingIcon = {
                /**
                 * Icono dropdown.
                 */
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),  // Conecta con dropdown
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        /**
         * Menú dropdown con opciones.
         */
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            /**
             * Item por cada región.
             */
            regiones.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region.nombre) },
                    onClick = {
                        onRegionSelected(region.nombre)
                        expanded = false
                    }
                )
            }
        }
    }
}