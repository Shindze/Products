package com.example.products.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.products.model.Product
import com.example.products.ui.theme.nunitoFontFamily
import com.example.products.viewmodel.Factory.ProductViewModelFactory
import com.example.products.viewmodel.ProductViewModel
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.appstate.ProductManager

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(LocalContext.current)
    )
) {

    val widgets = Widgets()
    val appState = AppStateManager.status.collectAsState().value

    val qurrentProduct = ProductManager.currentProduct.collectAsState().value.currentProduct

    val product = viewModel.listOfProducts.collectAsState().value.listProducts!![qurrentProduct - 1]

    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Подробнее",
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },

        ) { innerPadding ->
        when (appState) {
            AppState.LOADING -> widgets.CustomCircularProgressBar()
            AppState.SUCCESS -> ProductDescription(
                Modifier.padding(innerPadding), product
            )

            AppState.ERROR -> widgets.EmptyText()
        }
    }
}

@Composable
private fun ProductDescription(modifier: Modifier, product: Product) {
    Column(
        modifier
            .clip(RoundedCornerShape(24.dp))
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {

            if (product.images.size > 1) {
                Row(
                    Modifier
                        .horizontalScroll(ScrollState(0))
                        .padding(16.dp),
                ) {
                    product.images.mapIndexed { index, image ->
                        if (product.images.size > 1) {
                            AsyncImage(
                                model = image,
                                contentDescription = "Product Image",
                                modifier = Modifier
                                    .height(345.dp)
                                    .width(246.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                            if (index != product.images.lastIndex) {
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                }
            } else {
                product.images.mapIndexed { _, image ->
                    AsyncImage(
                        model = image,
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .height(345.dp)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {

            val discountPercentage = product.discountPercentage ?: 0.0

            val originalPrice = if (discountPercentage > 0.0) {
                product.price / (1 - discountPercentage / 100)
            } else {
                product.price
            }

            Text(
                text = "$${product.price}",
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.1f", originalPrice),
                fontFamily = nunitoFontFamily,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.align(Alignment.Top)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = product.title,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
        Text(
            text = "${product.rating.toString()}★",
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = product.description,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}
