package com.example.products.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.products.view.MainProductsScreen
import com.example.products.view.ProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.MainScren.route) {
        composable(route = Screens.MainScren.route) {
            MainProductsScreen(navController = navController)
        }
        composable(route = Screens.ProductScreen.route) {
            ProductScreen(navController = navController)
        }
    }
}