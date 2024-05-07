package com.example.products.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.products.model.Product
import com.example.products.navigation.Screens
import com.example.products.ui.theme.nunitoFontFamily
import com.example.products.viewmodel.Factory.ProductsViewModelFactory
import com.example.products.viewmodel.ListOfProductsViewModel
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.ProductsUiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfProductsScreen(
    navController: NavController, viewModel: ListOfProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(LocalContext.current)
    )
) {

    val widgets = Widgets()
    val appState = AppStateManager.status.collectAsState().value

    val listOfResponseData = viewModel.listOfProducts.collectAsState().value
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = appState == AppState.LOADING)

    Log.e("ListOfProductsScreen:", "Пересборка вью")

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
                        text = "Продукты",
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
            )
        },
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.updateAllProductsData()
            },
        ) {
            ScreenBody(
                Modifier.padding(innerPadding),
                navController,
                listOfResponseData,
                viewModel,
                appState,
                widgets
            )
        }
    }
}

@Composable
private fun ScreenBody(
    modifier: Modifier,
    navController: NavController,
    listOfResponseData: ProductsUiState,
    viewModel: ListOfProductsViewModel,
    appState: AppState,
    widgets: Widgets,

    ) {
    Column(modifier.fillMaxWidth()) {
        RowOfCategories(viewModel, listOfResponseData)
        when (appState) {
            AppState.LOADING -> widgets.CustomCircularProgressBar()
            AppState.SUCCESS -> ListOfProducts(listOfResponseData, navController, viewModel)
            AppState.ERROR -> widgets.EmptyText()
        }
    }
}

@Composable
private fun NavigationButton(viewModel: ListOfProductsViewModel) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .width(128.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    if (ProductManager.currentPage.value.currentPage > 1) ProductManager.updateCurrentPage(
                        ProductManager.currentPage.value.currentPage - 1
                    )
                    viewModel.changePage(false)
                }, Alignment.Center
        ) {
            Text(
                text = "Назад",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Box(
            modifier = Modifier
                .height(48.dp)
                .width(128.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    if (ProductManager.currentPage.value.currentPage < 5) {
                        ProductManager.updateCurrentPage(ProductManager.currentPage.value.currentPage + 1)
                    }
                    viewModel.changePage(true)
                }, Alignment.Center
        ) {
            Text(
                text = "Вперед",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowOfCategories(
    viewModel: ListOfProductsViewModel, listOfResponseData: ProductsUiState
) {
    LazyRow(Modifier.padding(start = 4.dp)) {
        if (listOfResponseData.listCategories != null) {
            items(listOfResponseData.listCategories) { category ->
                val isSelected = viewModel.listOfProducts.value.selectedCategoriesToChipState?.get(
                    category
                ) ?: false
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    onClick = {
                        viewModel.setFilterChipState(!isSelected, category)
                    },
                    label = {
                        Text(category)
                    },
                    selected = isSelected,
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

@Composable
private fun ListOfProducts(
    listOfResponseData: ProductsUiState,
    navController: NavController,
    viewModel: ListOfProductsViewModel
) {

    val scrollState = rememberLazyListState()

    val showButton = remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState, modifier = Modifier.padding(horizontal = 12.dp)) {
            if (listOfResponseData.listProducts != null) {
                items(listOfResponseData.listProducts) { product ->
                    ProductCard(navController = navController, product = product, ProductManager)
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                NavigationButton(viewModel)
            }
        }

        if (showButton.value) {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.SearchScreen.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        }
    }
}

@Composable
private fun CustomListItem(
    navController: NavController,
    product: Product,
    backgroundColor: Color,
    productManager: ProductManager
) {
    Spacer(modifier = Modifier.height(12.dp))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                productManager.updateCurrentProduct(product.id)
                productManager.updateSearchNavigate(false)
                navController.navigate(route = Screens.ProductScreen.route)
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
    navController: NavController, product: Product, productManager: ProductManager
) {
    CustomListItem(
        navController = navController,
        product = product,
        backgroundColor = MaterialTheme.colorScheme.primary,
        productManager
    )
}