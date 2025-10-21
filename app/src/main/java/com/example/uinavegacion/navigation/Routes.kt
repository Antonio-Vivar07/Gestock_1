package com.example.uinavegacion.navigation

sealed class Route(val path: String) {
    data object Home : Route("welcome")
    data object Login : Route("login")
    data object Register : Route("register")
    data object ProductMenu : Route("product_menu")
    data object ProductEntry : Route("product_entry")
    data object Stock : Route("stock")
    data object InventoryControl : Route("inventory_control")
    data object CreateProduct : Route("create_product")
    data object ProductCreatedSuccess : Route("product_created_success")
    data object SearchProduct : Route("search_product")
    data object ReportsDashboard : Route("reports_dashboard")

    // --- NUEVAS RUTAS PARA ORDEN DE COMPRA ---
    data object CreatePurchaseOrder : Route("create_purchase_order")
    data object PurchaseOrderSuccess : Route("purchase_order_success")
}
