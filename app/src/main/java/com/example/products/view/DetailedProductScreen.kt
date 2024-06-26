package com.example.products.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.products.model.Product
import com.example.products.ui.theme.nunitoFontFamily
import com.example.products.viewmodel.DetailedProductViewModel
import com.example.products.viewmodel.Factory.ProductViewModelFactory
import com.example.products.viewmodel.uiState.AppState

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    viewModel: DetailedProductViewModel = viewModel(
        factory = ProductViewModelFactory(LocalContext.current)
    ),
    productId: String,
) {

    val appState = viewModel.listOfProducts.collectAsState().value.appState

    val productsState = viewModel.listOfProducts.collectAsState().value
    val product = productsState.listProducts?.find { it.id == productId.toInt() }

    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                title = {
                    Text(
                        text = "Подробнее",
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
            AppState.INITIAL -> SkeletonProduct(modifier = Modifier.padding(innerPadding))
            AppState.LOADING -> SkeletonProduct(modifier = Modifier.padding(innerPadding))

            AppState.SUCCESS -> product?.let {
                ProductDescription(
                    Modifier.padding(innerPadding), it
                )
            }

            AppState.ERROR -> ErrorText(modifier = Modifier.padding(innerPadding))
        }
    }
}

@SuppressLint("DefaultLocale")
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
                .background(MaterialTheme.colorScheme.surface)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 22.sp
        )
        Text(
            text = "${product.rating.toString()}★",
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = product.description,
            textAlign = TextAlign.Justify,
            fontFamily = nunitoFontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun SkeletonProduct(modifier: Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f), Color.LightGray, Color.LightGray.copy(alpha = 0.3f)
    )
    val transition = rememberInfiniteTransition(label = "")
    val translateAnim = transition.animateFloat(
        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
            tween(durationMillis = 1200, easing = LinearEasing), RepeatMode.Restart
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Surface(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        color = MaterialTheme.colorScheme.outline,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .height(345.dp)
                        .width(246.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .height(345.dp)
                        .width(246.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .width(250.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush),
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ErrorText(
    modifier: Modifier
) {
    Spacer(modifier.height(64.dp))
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Упс, тут ничего нет :(",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
    }
}