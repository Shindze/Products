package com.example.products.view

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.appstate.ProductManager

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

    val widgets = Widgets()
    val appState = AppStateManager.status.collectAsState().value
    var text by remember { mutableStateOf(viewModel.textFieldValue) }

    val listOfProducts = viewModel.listOfProducts.collectAsState().value.listSearchProducts

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
                        cursorColor = Color.Transparent,
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),

                    singleLine = true,
                    placeholder = { Text("Введите запрос") },
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { viewModel.searchItems() }, Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Localized description"
                    )
                }
            }
            when (appState) {
                AppState.LOADING -> widgets.CustomCircularProgressBar()
                AppState.SUCCESS -> ScreenBody(
                    Modifier.padding(innerPadding),
                    navController,
                    listOfProducts,
                )

                AppState.ERROR -> widgets.EmptyText()
            }
        }
    }
}

@Composable
private fun ScreenBody(
    modifier: Modifier,
    navController: NavController,
    listOfProducts: List<Product>?,
) {
    Box(modifier.fillMaxSize()) {
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
    productManager: ProductManager
) {

    Spacer(modifier = Modifier.height(12.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                productManager.updateCurrentProduct(product.id)
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