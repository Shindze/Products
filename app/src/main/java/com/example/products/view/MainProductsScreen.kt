package com.example.products.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.products.model.Product
import com.example.products.navigation.Screens
import com.example.products.ui.theme.nunitoFontFamily
import com.example.products.viewmodel.ProductsViewModel
import com.example.products.viewmodel.Factory.ProductsViewModelFactory
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.appstate.ProductManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProductsScreen(
    navController: NavController, viewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(LocalContext.current)
    )
) {

    val widgets = Widgets()
    val appState = AppStateManager.status.collectAsState().value

    val listOfProducts = viewModel.listOfProducts.collectAsState().value.listProducts
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = appState == AppState.LOADING)

    Log.e("Состояние вью:", appState.toString())

    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(
                    text = "Продукты",
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            })
        },
    ) { innerPadding ->
        when (appState) {
            AppState.LOADING -> widgets.CustomCircularProgressBar()
            AppState.SUCCESS -> SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    viewModel.updateProducts()
                },
            ) {
                ScreenBody(
                    Modifier.padding(innerPadding), navController, listOfProducts
                )
            }

            AppState.ERROR -> SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    viewModel.updateProducts()
                },
            ) {
                widgets.EmptyText()
            }
        }
    }
}

@Composable
private fun ScreenBody(
    modifier: Modifier, navController: NavController, listOfProducts: List<Product>?
) {
    Box(modifier) {
        LazyColumn(Modifier.padding(horizontal = 12.dp)) {
            if (listOfProducts != null) {
                items(listOfProducts) { product ->
                    ProductCard(
                        navController = navController, product = product, ProductManager
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomListItem(
    navController: NavController,
    product: Product,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    ProductManager: ProductManager
) {

    Spacer(modifier = Modifier.height(12.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                ProductManager.updateCurrentProduct(product.id)
                navController.navigate(Screens.ProductScreen.route)
            }, color = backgroundColor, shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun ProductCard(
    navController: NavController, product: Product, ProductManager: ProductManager
) {
    CustomListItem(
        navController = navController,
        product = product,
        backgroundColor = MaterialTheme.colorScheme.primary,
        ProductManager
    )
}