package com.example.uinavegacion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.uinavegacion.ui.components.AppDrawer
import com.example.uinavegacion.ui.components.AppTopBar
import com.example.uinavegacion.ui.components.defaultDrawerItems
import com.example.uinavegacion.ui.screen.*
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController) {
    val authVm: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val session by authVm.session.collectAsState()
    val isLoggedIn = session != null

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
            val title = when (currentRoute?.destination?.route) {
                Route.Home.path -> "Bienvenido a Gestock"
                Route.Login.path -> "Inicio de Sesión"
                Route.Register.path -> "Registro de Usuario"
                else -> "Gestock"
            }
            AppTopBar(title = title, navController = navController, onOpenDrawer = null)
        }
    ) { innerPadding ->
        NavHost(navController, Route.Home.path, Modifier.padding(innerPadding)) {
            composable(Route.Home.path) { WelcomeScreen({ navController.navigate(Route.Login.path) }, { navController.navigate(Route.Register.path) }) }
            composable(Route.Login.path) { LoginScreen(authVm, {}, { navController.navigate(Route.Register.path) }) }
            composable(Route.Register.path) { RegisterScreen(authVm, onGoLogin = { navController.navigate(Route.Login.path) }) }
        }
    }
}

@Composable
private fun PrivateNavGraph(navController: NavHostController, authVm: AuthViewModel) {
    val productVm: ProductViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val goToInventoryControl: (String?) -> Unit = { navController.navigate(Route.InventoryControl.path) { popUpTo(0) } }
    val onLogout: () -> Unit = { authVm.logout() }
    val goToCreateProduct = { navController.navigate(Route.CreateProduct.path) }
    val goToMovements = { navController.navigate(Route.Movements.path) }
    val goToInventoryList = { navController.navigate(Route.InventoryList.path) }
    val goToSearchAndScan = { navController.navigate(Route.SearchScan.path) }
    val goToReports = { navController.navigate(Route.Reports.path) }
    val goToUsers = { navController.navigate(Route.Users.path) }
    val goToQrScanner = { navController.navigate(Route.QrScanner.path) }
    val goToProductDetailById: (Int) -> Unit = { id -> navController.navigate("${Route.ProductDetail.path}?productId=$id") }
    val goToProductDetailByCode: (String) -> Unit = { code -> navController.navigate("${Route.ProductDetail.path}?productCode=$code") }
    val goToProductSuccess: (String, String) -> Unit = { name, code -> navController.navigate("${Route.ProductCreatedSuccess.path}/$name/$code") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    isLoggedIn = true,
                    onHome = { scope.launch { drawerState.close() }; goToInventoryControl(null) },
                    onLogin = { /* No-op */ },
                    onRegister = { scope.launch { drawerState.close() }; goToUsers() },
                    onGoToCreateProduct = { scope.launch { drawerState.close() }; goToCreateProduct() },
                    onGoToMovements = { scope.launch { drawerState.close() }; goToMovements() },
                    onGoToInventoryList = { scope.launch { drawerState.close() }; goToInventoryList() },
                    onGoToSearchAndScan = { scope.launch { drawerState.close() }; goToSearchAndScan() },
                    onGoToReports = { scope.launch { drawerState.close() }; goToReports() },
                    onLogout = { scope.launch { drawerState.close() }; onLogout() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                val currentRoute by navController.currentBackStackEntryAsState()
                val title = when (currentRoute?.destination?.route?.substringBefore('?')) {
                    Route.InventoryControl.path -> "Control de Inventario"
                    Route.CreateProduct.path -> "Ingresar Producto"
                    Route.Movements.path -> "Movimientos de Stock"
                    Route.InventoryList.path -> "Inventario"
                    Route.SearchScan.path -> "Buscar / Escanear"
                    Route.Reports.path -> "Reportes"
                    Route.Users.path -> "Usuarios y Roles"
                    Route.QrScanner.path -> "Escaneando..."
                    Route.ProductDetail.path -> "Detalle de Producto"
                    Route.ProductCreatedSuccess.path.substringBefore('/') -> "Éxito"
                    else -> "Gestock"
                }
                AppTopBar(title = title, navController = navController, onOpenDrawer = { scope.launch { drawerState.open() } })
            }
        ) { innerPadding ->
            NavHost(navController, Route.InventoryControl.path, Modifier.padding(innerPadding)) {
                composable(Route.InventoryControl.path) { InventoryControlScreen(null, authVm, goToUsers, goToCreateProduct, goToMovements, goToInventoryList, goToReports, goToSearchAndScan) }
                composable(Route.CreateProduct.path) { CreateProductScreen(authVm, productVm, goToProductSuccess) }
                composable(Route.Movements.path) { ProductEntryScreen(authVm, productVm) }
                composable(Route.InventoryList.path) { StockSearchScreen(productVm, onProductClick = goToProductDetailById) }
                composable(Route.Reports.path) { ReportsDashboardScreen() }
                composable(Route.Users.path) { RegisterScreen(authVm, onGoLogin = null) }
                composable(Route.SearchScan.path) { ProductQueryScreen(productVm, goToProductDetailById, goToQrScanner) }
                composable(Route.QrScanner.path) { QrScannerScreen { scannedCode -> navController.popBackStack(); goToProductDetailByCode(scannedCode) } }

                composable(
                    route = "${Route.ProductDetail.path}?productId={productId}&productCode={productCode}",
                    arguments = listOf(
                        navArgument("productId") { type = NavType.IntType; defaultValue = -1 },
                        navArgument("productCode") { type = NavType.StringType; nullable = true }
                    )
                ) { backStackEntry ->
                    ProductDetailScreen(
                        productId = backStackEntry.arguments?.getInt("productId")?.takeIf { it != -1 },
                        productCode = backStackEntry.arguments?.getString("productCode"),
                        authVm = authVm,
                        productVm = productVm,
                        onBack = { navController.popBackStack() },
                        onGoToInventoryControl = { goToInventoryControl(null) }
                    )
                }

                composable(
                    route = "${Route.ProductCreatedSuccess.path}/{productName}/{productCode}",
                    arguments = listOf(
                        navArgument("productName") { type = NavType.StringType },
                        navArgument("productCode") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    ProductCreatedSuccessScreen(
                        productName = backStackEntry.arguments?.getString("productName") ?: "",
                        productCode = backStackEntry.arguments?.getString("productCode") ?: "",
                        onGoToInventoryControl = { goToInventoryControl(null) },
                        onAddAnotherProduct = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
