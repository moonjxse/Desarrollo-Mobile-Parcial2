package com.example.tiendapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tiendapp.data.Order
import com.example.tiendapp.data.Product
import com.example.tiendapp.data.User
import com.example.tiendapp.viewmodel.HomeViewModel
import com.example.tiendapp.viewmodel.ProductViewModel
import com.example.tiendapp.viewmodel.OrderViewModel
import com.example.tiendapp.viewmodel.UserViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla principal de la aplicación.
 * Muestra un carrusel, botones de navegación, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel,
    onNavigateToContact: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProducts: () -> Unit
) {
    val productos by productViewModel.allProducts.collectAsState(initial = emptyList())
    val currentUser by userViewModel.currentUser.collectAsState()
    val orderMessage by orderViewModel.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ToxcitryPC",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bienvenido a ToxcitryPC",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                ImageCarousel(homeViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botones principales
            item {
                AnimatedButton(text = "Ver Perfil", onClick = onNavigateToProfile)
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedButton(text = "Ver Mis Pedidos", onClick = onNavigateToOrders)
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedButton(text = "Formulario de Contacto", onClick = onNavigateToContact)
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedButton(text = "Ver productos", onClick = onNavigateToProducts)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Mensaje de confirmación de compra
            item {
                orderMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                    LaunchedEffect(Unit) {
                        delay(2000)
                        orderViewModel.clearMessage()
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCarousel(homeViewModel: HomeViewModel) {
    val images = homeViewModel.getCarouselImages()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })

    // Auto-deslizamiento cada 3 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            //  Aquí usamos imágenes locales desde drawable
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Imagen ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(images.size) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                    .background(
                        if (pagerState.currentPage == index)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Gray,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

@Composable
fun ProductCardWithBuyButton(
    producto: Product,
    currentUser: User?,
    onComprar: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(producto.imagenUrl ?: ""),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Precio: $${producto.precio}", color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { if (currentUser != null) onComprar(producto) },
                    enabled = currentUser != null
                ) {
                    Text("Comprar")
                }
            }
        }
    }
}

@Composable
fun AnimatedButton(text: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(100),
        label = "button_scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(56.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}
