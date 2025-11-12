package com.example.tiendapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tiendapp.view.*
import com.example.tiendapp.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Orders : Screen("orders")
    object Contact : Screen("contact")
    object Products : Screen("productos")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val appContext = LocalContext.current.applicationContext as android.app.Application

    // ViewModels que necesitan acceso a Room (tienen Application)
    val userViewModel: UserViewModel = viewModel(
        factory = viewModelFactory {
            initializer { UserViewModel(appContext) }
        }
    )

    val orderViewModel: OrderViewModel = viewModel(
        factory = viewModelFactory {
            initializer { OrderViewModel(appContext) }
        }
    )

    val contactViewModel: ContactViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ContactViewModel(appContext) }
        }
    )

    // ViewModels simples (sin Application)
    val homeViewModel: HomeViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()

    // Navegacin principal
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { success ->
                    if (success) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        // Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                userViewModel = userViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Principal (Home)
        composable(Screen.Home.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                userViewModel = userViewModel,
                productViewModel = productViewModel,
                orderViewModel = orderViewModel,
                onNavigateToContact = { navController.navigate(Screen.Contact.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToProducts = { navController.navigate(Screen.Products.route) }
            )
        }

        // Pantalla de Perfil
        composable(Screen.Profile.route) {
            ProfileScreen(userViewModel = userViewModel)
        }

        // Pantalla de Pedidos
        composable(Screen.Orders.route) {
            val user by userViewModel.currentUser.collectAsState()
            OrderListScreen(
                orderViewModel = orderViewModel,
                userId = user?.id,
                isAdmin = user?.isAdmin ?: false
            )
        }

        // Pantalla de Contacto
        composable(Screen.Contact.route) {
            ContactFormScreen(
                contactViewModel = contactViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla de Productos
        composable(Screen.Products.route) {
            ProductListScreen(
                productViewModel = productViewModel,
                onOrderProduct = { product ->
                    val user = userViewModel.currentUser.value
                    if (user != null) {
                        val newOrder = com.example.tiendapp.data.Order(
                            userId = user.id,
                            productId = product.id,
                            cantidad = 1,
                            total = product.precio
                        )
                        orderViewModel.insertOrder(newOrder)
                    }
                }
            )
        }
    }
}
