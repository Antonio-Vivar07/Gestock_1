package com.example.uinavegacion.navigation

sealed class Route(val path: String) {
    // --- Rutas Públicas ---
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    
    // --- Rutas Privadas (Menú Principal) ---
    data object InventoryControl : Route("inventory_control")
    data object CreateProduct : Route("product_create")
    data object Movements : Route("movements")
    data object InventoryList : Route("inventory_list")
    data object SearchScan : Route("search_scan")
    data object Reports : Route("reports")
    data object Users : Route("users")
    data object RemotePosts : Route("remote_posts")

    // --- Rutas de Flujo (Secundarias) ---
    data object QrScanner : Route("qr_scanner")
    data object ProductDetail : Route("product_detail")
    data object ProductCreatedSuccess : Route("product_created_success")

    // --- Rutas Antiguas (pueden ser eliminadas si ya no se usan) ---
    data object ProductMenu : Route("product_menu")
    data object ProductEntry : Route("product_entry")
    data object Stock : Route("stock")
    data object SearchProduct : Route("search_product")
    data object ReportsDashboard : Route("reports_dashboard")
    data object CreatePurchaseOrder : Route("create_purchase_order")
    data object PurchaseOrderSuccess : Route("purchase_order_success")
}
