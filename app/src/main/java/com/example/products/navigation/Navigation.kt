package com.example.products.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.products.view.ListOfProductsScreen
import com.example.products.view.ProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.MainScreen.route) {
        composable(route = Screens.MainScreen.route) {
            ListOfProductsScreen(navController = navController)
        }
        composable(route = "${Screens.ProductScreen.route}/{product_Id}",
            arguments = listOf(
                navArgument("product_Id") {
                    type = NavType.StringType
                },
            )) {
            val prodId = it.arguments?.getString("product_Id") ?: ""
            ProductScreen(navController = navController, productId = prodId)
        }
    }
}