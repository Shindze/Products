package com.example.products.navigation

sealed class Screens(val route: String) {
    data object MainScreen : Screens("main_screen")
    data object ProductScreen : Screens("Product_screen")
}