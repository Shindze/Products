package com.example.products.navigation

sealed class Screens(val route: String) {
    data object MainScren : Screens("main_screen")
    data object SearchScreen : Screens("search_screen")
    data object ProductScreen : Screens("Product_screen")
}