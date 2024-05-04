package com.example.products.navigation

sealed class Screens(val route: String){
    object MainScren : Screens("main_screen")
    object SearchScreen : Screens("search_screen")
    object ProductScreen : Screens("product_screen")
}