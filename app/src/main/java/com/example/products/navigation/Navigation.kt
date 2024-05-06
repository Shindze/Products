package com.example.products.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.products.view.ListOfProductsScreen
import com.example.products.view.ProductScreen
import com.example.products.view.SearchScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.MainScren.route) {
        composable(route = Screens.MainScren.route) {
            ListOfProductsScreen(navController = navController)
        }
        composable(route = Screens.ProductScreen.route) {
            ProductScreen(navController = navController)
        }

        composable(route = Screens.SearchScreen.route) {
            SearchScreen(navController = navController)
        }
    }
}