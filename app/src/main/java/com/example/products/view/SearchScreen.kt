package com.example.products.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.products.model.Product
import com.example.products.navigation.Screens
import com.example.products.ui.theme.nunitoFontFamily
import com.example.products.viewmodel.Factory.SearchViewModelFactory
import com.example.products.viewmodel.SearchViewModel
import com.example.products.viewmodel.uiState.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = SearchViewModelFactory(
            LocalContext.current
        )
    )
) {

    val appState = viewModel.listOfProducts.collectAsState().value.appState

    var text by remember { mutableStateOf(viewModel.textFieldValue) }

    val listOfProducts = viewModel.listOfProducts.collectAsState().value.listProducts

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
                        text = "Поиск",
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
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
        Column(
            Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        ) {
            Row(Modifier.fillMaxWidth()) {
                val keyboardController = LocalSoftwareKeyboardController.current
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        viewModel.textFieldValue = text
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),

                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),

                    keyboardActions = KeyboardActions(onNext = {
                        keyboardController?.hide()
                        viewModel.searchItems()
                    }),

                    singleLine = true,
                    placeholder = { Text("Введите запрос") },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            SearchScreenBody(
                navController, listOfProducts, appState
            )
        }
    }
}


@Composable
private fun SearchScreenBody(
    navController: NavController,
    listOfProducts: List<Product>?,
    appState: AppState,
) {

    LazyColumn(Modifier.fillMaxSize()) {

        if (appState == AppState.LOADING) {
            items(20) {
                SkeletonListItemSearch()
            }
        }

        if (appState == AppState.SUCCESS && listOfProducts.isNullOrEmpty()) {
            item {
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
        }

        if (!listOfProducts.isNullOrEmpty()) {
            items(listOfProducts) { product ->
                SearchProductCard(
                    navController = navController, product = product
                )
            }
        } else if (appState == AppState.ERROR) {
            item {
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
        }
    }
}

@Composable
private fun CustomListItem(
    navController: NavController,
    product: Product,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
) {

    Spacer(modifier = Modifier.height(12.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    route = "Product_screen/${product.id}"
                ) {
                    popUpTo(Screens.SearchScreen.route) {
                        inclusive = false
                    }
                }
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
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun SearchProductCard(
    navController: NavController, product: Product
) {
    CustomListItem(
        navController = navController,
        product = product,
        backgroundColor = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun SkeletonListItemSearch() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f), Color.LightGray, Color.LightGray.copy(alpha = 0.3f)
    )
    val transition = rememberInfiniteTransition()
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

    Spacer(modifier = Modifier.height(12.dp))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = false) {},
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(brush)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Spacer(
                modifier = Modifier
                    .width(40.dp)
                    .height(16.dp)
                    .background(brush)
            )
        }
    }
}