package com.example.tiendapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tiendapp.data.Order
import com.example.tiendapp.viewmodel.OrderViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    orderViewModel: OrderViewModel,
    userId: Int? = null,
    isAdmin: Boolean = false
) {
    val allOrders by orderViewModel.allOrders.collectAsState()

    val userOrders by orderViewModel.userOrders.collectAsState()

    LaunchedEffect(userId) {
        if (!isAdmin && userId != null) {
            orderViewModel.loadOrdersByUser(userId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isAdmin) "Pedidos - Admin" else "Mis Pedidos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        val ordersToShow = if (isAdmin) allOrders else userOrders

        if (ordersToShow.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(ordersToShow) { order ->
                    OrderItem(order)
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID Pedido: ${order.id}", fontWeight = FontWeight.Bold)
            Text("Producto ID: ${order.productId}")
            Text("Cantidad: ${order.cantidad}")
            Text("Estado: ${order.estado}")
            Text("Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(order.fecha)}")
        }
    }
}
