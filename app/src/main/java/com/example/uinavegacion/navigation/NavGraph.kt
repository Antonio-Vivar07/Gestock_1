package com.example.uinavegacion.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.uinavegacion.ui.components.AppDrawer
import com.example.uinavegacion.ui.components.AppTopBar
import com.example.uinavegacion.ui.components.defaultDrawerItems
import com.example.uinavegacion.ui.screen.*
import com.example.uinavegacion.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController, authVm: AuthViewModel = viewModel()) {

    val isLoggedIn = authVm.session != null

    if (isLoggedIn) {
        PrivateNavGraph(navController = navController, authVm = authVm)
    } else {
        PublicNavGraph(navController = navController, authVm = authVm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublicNavGraph(navController: NavHostController, authVm: AuthViewModel) {
    Scaffold(
        topBar = {
            val currentRoute by navController.currentBackStackEntryAsState()
            CenterAlignedTopAppBar(
                title = { Text("Demo Navegación Compose") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    if (currentRoute?.destination?.route != Route.Home.path) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding -> 
        NavHost(
            navController = navController, 
            startDestination = Route.Home.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.path) {
                WelcomeScreen(
                    onGoLogin = { navController.navigate(Route.Login.path) },
                    onGoRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.Login.path) {
                LoginScreen(
                    onLoginOk = { authVm.login("trabajador 1") },
                    onGoRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.Register.path) {
                RegisterScreen(onGoLogin = { navController.navigate(Route.Login.path) })
            }
        }
    }
}

@Composable
private fun PrivateNavGraph(navController: NavHostController, authVm: AuthViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- DEFINICIÓN DE RUTAS SEPARADAS ---
    val stockSearchRoute = "stock_search_route"

    // --- Acciones de Navegación del Grafo Privado ---
    val goToInventoryControl: (String?) -> Unit = { message ->
        val route = if (message != null) "${Route.InventoryControl.path}?message=$message" else Route.InventoryControl.path
        navController.navigate(route) { popUpTo(0) }
    }
    val onLogout: () -> Unit = { authVm.logout() }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goProductMenu: () -> Unit = { navController.navigate(Route.ProductMenu.path) }
    val goProductEntry: () -> Unit = { navController.navigate(Route.ProductEntry.path) }
    val goToReportsDashboard: () -> Unit = { navController.navigate(Route.ReportsDashboard.path) }
    val goProductQuery: () -> Unit = { navController.navigate(Route.SearchProduct.path) } // 'Buscar producto' va a la pantalla de consulta
    val goStockSearch: () -> Unit = { navController.navigate(stockSearchRoute) } // 'Stock' va a la pantalla de lista
    val goToCreateProduct: () -> Unit = { navController.navigate(Route.CreateProduct.path) }
    val goToStockDetail: (String) -> Unit = { productId -> navController.navigate("${Route.Stock.path}/$productId") }
    val goToCreatePurchaseOrder: (String) -> Unit = { productId -> navController.navigate("${Route.CreatePurchaseOrder.path}/$productId") }
    val goToProductSuccess: (String) -> Unit = { productName -> navController.navigate("${Route.ProductCreatedSuccess.path}/$productName") }
    val goToPurchaseOrderSuccess: (String, String, Int) -> Unit = { productId, provider, quantity -> navController.navigate("${Route.PurchaseOrderSuccess.path}/$productId/$provider/$quantity") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    isLoggedIn = true,
                    onHome = { scope.launch { drawerState.close() }; goToInventoryControl(null) }, 
                    onLogin = {}, 
                    onRegister = { scope.launch { drawerState.close() }; goRegister() }, 
                    onGoToProductMenu = { scope.launch { drawerState.close() }; goProductMenu() },
                    onGoToProductEntry = { scope.launch { drawerState.close() }; goProductEntry() },
                    onGoToReports = { scope.launch { drawerState.close() }; goToReportsDashboard() },
                    onGoToSearch = { scope.launch { drawerState.close() }; goProductQuery() },
                    onLogout = { scope.launch { drawerState.close() }; onLogout() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = { AppTopBar(navController = navController, onOpenDrawer = { scope.launch { drawerState.open() } }) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.InventoryControl.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = "${Route.InventoryControl.path}?message={message}",
                    arguments = listOf(navArgument("message") { type = NavType.StringType; nullable = true })
                ) {
                     val message = it.arguments?.getString("message")
                    InventoryControlScreen(
                        message = message,
                        onGoToRegister = goRegister,
                        onGoToProductMenu = goProductMenu,
                        onGoToProductEntry = goProductEntry,
                        onGoToReports = goToReportsDashboard,
                        onGoToSearch = goProductQuery, // <-- CONECTADO A LA PANTALLA DE CONSULTA
                        onGoToStock = goStockSearch     // <-- CONECTADO A LA PANTALLA DE LISTA DE STOCK
                    )
                }

                composable(Route.Register.path) { RegisterScreen(onGoLogin = { navController.navigate(Route.Login.path) }) }
                composable(Route.ProductMenu.path) { ProductMenuScreen(onGoRegisterProduct = goToCreateProduct) }
                composable(Route.CreateProduct.path) { CreateProductScreen(onCreateProduct = goToProductSuccess) }
                
                // --- PANTALLA DE CONSULTA PARA "BUSCAR PRODUCTO" ---
                composable(Route.SearchProduct.path) { ProductQueryScreen() }

                // --- PANTALLA DE LISTA PARA "STOCK" ---
                composable(stockSearchRoute) { SearchProductScreen(onProductClick = goToStockDetail) }
                
                composable(Route.ProductEntry.path) { ProductEntryScreen() }
                composable(Route.ReportsDashboard.path) { ReportsDashboardScreen() }
                
                composable(
                    route = "${Route.ProductCreatedSuccess.path}/{productName}",
                    arguments = listOf(navArgument("productName") { type = NavType.StringType })
                ) { backStackEntry ->
                    ProductCreatedSuccessScreen(
                        productName = backStackEntry.arguments?.getString("productName") ?: "", 
                        onGoToInventoryControl = { goToInventoryControl(null) },
                        onAddAnotherProduct = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${Route.Stock.path}/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: ""
                    StockScreen(productId = productId, onGenerateOrder = { goToCreatePurchaseOrder(productId) })
                }
                composable(
                    route = "${Route.CreatePurchaseOrder.path}/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: ""
                    CreatePurchaseOrderScreen(productId = productId, onOrderGenerated = goToPurchaseOrderSuccess)
                }
                composable(
                    route = "${Route.PurchaseOrderSuccess.path}/{productId}/{provider}/{quantity}",
                    arguments = listOf(
                        navArgument("productId") { type = NavType.StringType },
                        navArgument("provider") { type = NavType.StringType },
                        navArgument("quantity") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: ""
                    val provider = backStackEntry.arguments?.getString("provider") ?: ""
                    val quantity = backStackEntry.arguments?.getInt("quantity") ?: 0
                    PurchaseOrderSuccessScreen(
                        productId = productId,
                        provider = provider,
                        quantity = quantity,
                        onBackToStock = { navController.popBackStack() },
                        onGoToInventoryControl = { goToInventoryControl(null) }
                    )
                }
            }
        }
    }
}