package com.example.sakura_fusion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.reserva.Reserva
import com.example.sakura_fusion.data.mesa.Mesa
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.ui.screens.*
import com.example.sakura_fusion.ui.theme.Sakura_fusionTheme
import com.example.sakura_fusion.ui.viewmodel.AppViewModel
import com.example.sakura_fusion.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sakura_fusionTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel()
    
    var userEmail by rememberSaveable { mutableStateOf(sessionManager.getUserEmail() ?: "") }
    val startDestination = if (sessionManager.isLoggedIn()) {
        when(sessionManager.getUserRole()) {
            "admin" -> "admin_main"
            "mesero" -> "waiter_main"
            else -> "main"
        }
    } else "login"

    val allOrders by appViewModel.pedidos.collectAsState()
    val allReservations by appViewModel.reservas.collectAsState()
    val allMesas by appViewModel.mesas.collectAsState()
    
    NavHost(
        navController = navController, 
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                appViewModel = appViewModel,
                onLoginSuccess = { role, email ->
                    sessionManager.saveSession(email, role)
                    userEmail = email
                    val startRoute = when(role) {
                        "admin" -> "admin_main"
                        "mesero" -> "waiter_main"
                        else -> "main"
                    }
                    navController.navigate(startRoute) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                appViewModel = appViewModel,
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { newUser ->
                    appViewModel.registrarUsuario(newUser)
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                appViewModel = appViewModel,
                onBack = { navController.popBackStack() },
                onResetSuccess = {
                    navController.navigate("login") {
                        popUpTo("forgot_password") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") { 
            ClientMainScreen(
                userEmail = userEmail,
                allOrders = allOrders,
                allReservations = allReservations,
                appViewModel = appViewModel,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") { popUpTo(0) }
                }
            ) 
        }
        composable("admin_main") { 
            AdminMainScreen(
                userEmail = userEmail,
                allOrders = allOrders,
                allReservations = allReservations.toMutableList(),
                appViewModel = appViewModel,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") { popUpTo(0) }
                }
            ) 
        }
        composable("waiter_main") { 
            WaiterMainScreen(
                userEmail = userEmail,
                allOrders = allOrders,
                allReservations = allReservations.toMutableList(),
                allMesas = allMesas,
                appViewModel = appViewModel,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") { popUpTo(0) }
                }
            ) 
        }
    }
}

@Composable
fun ClientMainScreen(
    userEmail: String,
    allOrders: List<Pedido>,
    allReservations: List<Reserva>,
    appViewModel: AppViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Menu, Screen.Reservas, Screen.Pedidos, Screen.Perfil)

    Scaffold(
        bottomBar = { AppBottomBar(navController, items) }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Menu.route, Modifier.padding(innerPadding)) {
            composable(Screen.Menu.route) { 
                MenuScreen(onOrderConfirmed = { nuevoPedido ->
                    val clientName = userEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Cliente"
                    appViewModel.crearPedido(nuevoPedido.copy(nombreCliente = clientName))
                }) 
            }
            composable(Screen.Reservas.route) { 
                ReservaScreen(
                    idUsuario = 1, 
                    allReservations = allReservations,
                    onNewReserva = { count -> navController.navigate("nueva_reserva/$count") }
                ) 
            }
            composable(
                "nueva_reserva/{count}",
                arguments = listOf(navArgument("count") { type = NavType.IntType })
            ) { backStackEntry ->
                val count = backStackEntry.arguments?.getInt("count") ?: 1
                TableSelectionScreen(
                    peopleCount = count,
                    onTableSelected = { mesa -> navController.navigate("reserva_final/${mesa.idMesa}/$count") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                "reserva_final/{idMesa}/{count}",
                arguments = listOf(
                    navArgument("idMesa") { type = NavType.IntType },
                    navArgument("count") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val idMesa = backStackEntry.arguments?.getInt("idMesa") ?: 0
                val count = backStackEntry.arguments?.getInt("count") ?: 1
                ReservaFormScreen(onBack = { fecha, hora, personas ->
                    if (fecha.isNotEmpty()) {
                        val clientName = userEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Cliente"
                        appViewModel.crearReserva(Reserva(
                            idReserva = 0,
                            fecha = fecha,
                            hora = hora,
                            cantPersonas = count,
                            idUsuario = 1,
                            idMesa = idMesa,
                            nombreCliente = clientName
                        ))
                        navController.navigate(Screen.Reservas.route) {
                            popUpTo("nueva_reserva/{count}") { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                })
            }
            composable(Screen.Pedidos.route) { 
                PedidosScreen(
                    idUsuario = 1, 
                    ordersList = allOrders,
                    onOrderClick = { id: Int -> navController.navigate("detalle_pedido/$id") }
                ) 
            }
            composable(
                "detalle_pedido/{idPedido}",
                arguments = listOf(navArgument("idPedido") { type = NavType.IntType })
            ) { backStackEntry ->
                val idPedido = backStackEntry.arguments?.getInt("idPedido") ?: 0
                OrderDetailScreen(idPedido = idPedido, ordersList = allOrders, onBack = { navController.popBackStack() })
            }
            composable(Screen.Perfil.route) { 
                ProfileScreen(
                    userEmail = userEmail,
                    onEditProfile = { navController.navigate("edit_profile") },
                    onLogout = onLogout
                ) 
            }
            composable("edit_profile") {
                EditProfileScreen(
                    userEmail = userEmail,
                    appViewModel = appViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun AdminMainScreen(
    userEmail: String,
    allOrders: List<Pedido>,
    allReservations: MutableList<Reserva>,
    appViewModel: AppViewModel,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Dashboard") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    label = { Text("Salir") },
                    selected = false,
                    onClick = onLogout
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { 
            AdminDashboardScreen(
                allOrders = allOrders, 
                allReservations = allReservations,
                onConfirmReserva = { appViewModel.confirmarReserva(it) },
                onClearOrders = { appViewModel.clearAllOrders() }
            )
        }
    }
}

@Composable
fun WaiterMainScreen(
    userEmail: String,
    allOrders: List<Pedido>,
    allReservations: MutableList<Reserva>,
    allMesas: List<Mesa>,
    appViewModel: AppViewModel,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, null) },
                    label = { Text("Mesas") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    label = { Text("Salir") },
                    selected = false,
                    onClick = onLogout
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { 
            WaiterDashboardScreen(
                allOrders = allOrders, 
                allReservations = allReservations,
                allMesas = allMesas,
                onDeliverOrder = { appViewModel.entregarPedido(it) }
            )
        }
    }
}

@Composable
fun AppBottomBar(navController: androidx.navigation.NavController, items: List<Screen>) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Menu : Screen("menu", "Menú", Icons.AutoMirrored.Filled.List)
    object Reservas : Screen("reservas", "Reservas", Icons.Default.DateRange)
    object Pedidos : Screen("pedidos", "Pedidos", Icons.Default.ShoppingCart)
    object Perfil : Screen("perfil", "Perfil", Icons.Default.Person)
}
