package com.example.tiendapp.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiendapp.data.Product
import com.example.tiendapp.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ProductListScreen
 *
 * @param productViewModel ViewModel que expone allProducts: Flow<List<Product>>
 * @param isAdmin indica si el usuario actual es administrador (muestra acciones extra)
 * @param onAddProduct callback al pulsar agregar producto (FAB)
 * @param onEditProduct callback con productId para editar (admin)
 * @param onViewProduct callback con productId para ver detalle
 * @param onOrderProduct callback con el objeto Product para iniciar pedido
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    productViewModel: ProductViewModel,
    isAdmin: Boolean = false,
    onAddProduct: () -> Unit = {},
    onEditProduct: (productId: Int) -> Unit = {},
    onViewProduct: (productId: Int) -> Unit = {},
    onOrderProduct: (product: Product) -> Unit = {}
) {
    val context = LocalContext.current
    val productsState = remember { mutableStateListOf<Product>() }

    // Recoger la lista desde el StateFlow / Flow del ViewModel
    LaunchedEffect(Unit) {
        productViewModel.allProducts.collectLatest { list ->
            productsState.clear()
            productsState.addAll(list)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Productos") })
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddProduct) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
            }
        }
    ) { padding ->
        if (productsState.isEmpty()) {
            // Mensaje cuando no hay productos
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos disponibles.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(productsState, key = { it.id }) { product ->
                ProductItem(
                    product = product,
                    isAdmin = isAdmin,
                    onClick = { onViewProduct(product.id) },
                    onOrder = {
                        if (product.stock <= 0) {
                            Toast.makeText(context, "Producto sin stock", Toast.LENGTH_SHORT).show()
                        } else {
                            // 🔹 Ahora se pasa el objeto completo Product
                            onOrderProduct(product)
                        }
                    },
                    onEdit = { onEditProduct(product.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onOrder: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Imagen (si existe)
            Box(modifier = Modifier.size(80.dp)) {
                if (!product.imagenUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imagenUrl,
                        contentDescription = product.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.nombre.firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.descripcion, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Precio: $${product.precio}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Acciones
            Column(horizontalAlignment = Alignment.End) {
                Button(onClick = onOrder, modifier = Modifier.height(36.dp)) {
                    Text("Pedir")
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (isAdmin) {
                    OutlinedButton(onClick = onEdit, modifier = Modifier.height(36.dp)) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}
