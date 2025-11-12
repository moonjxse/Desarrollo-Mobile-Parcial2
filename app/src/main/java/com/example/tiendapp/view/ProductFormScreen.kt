package com.example.tiendapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tiendapp.data.Product
import com.example.tiendapp.viewmodel.ProductViewModel

/**
 * Pantalla de formulario para crear o editar un producto
 *
 * @param productViewModel ViewModel que maneja la lógica de productos
 * @param product Producto existente (si se edita) o null (si es nuevo)
 * @param onSaved callback al guardar con éxito (vuelve a la lista o cierra formulario)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    product: Product? = null,
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val isEditing = product != null

    var nombre by remember { mutableStateOf(product?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(product?.descripcion ?: "") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var imagenUrl by remember { mutableStateOf(product?.imagenUrl ?: "") }

    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(if (isEditing) "Editar producto" else "Nuevo producto")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { if (it.matches(Regex("^[0-9]*\\.?[0-9]*\$"))) precio = it },
                label = { Text("Precio") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { if (it.matches(Regex("^[0-9]*\$"))) stock = it },
                label = { Text("Stock disponible") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                label = { Text("URL de la imagen (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val nombreValido = nombre.isNotBlank()
                    val precioValido = precio.isNotBlank() && precio.toDoubleOrNull() != null
                    val stockValido = stock.isNotBlank() && stock.toIntOrNull() != null

                    if (!nombreValido || !precioValido || !stockValido) {
                        Toast.makeText(context, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val nuevoProducto = Product(
                        id = product?.id ?: 0,
                        nombre = nombre.trim(),
                        descripcion = descripcion.trim(),
                        precio = precio.toInt(),
                        imagenUrl = imagenUrl.trim(),
                        stock = stock.toInt()
                    )

                    if (isEditing) {
                        productViewModel.updateProduct(nuevoProducto)
                        Toast.makeText(context, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        productViewModel.insertProduct(nuevoProducto)
                        Toast.makeText(context, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                    }

                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isEditing) "Actualizar producto" else "Guardar producto")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onSaved,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}

