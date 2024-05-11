package com.example.products.view

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.products.model.Product
import com.example.products.navigation.Screens
import com.example.products.ui.theme.nunitoFontFamily
import com.example.products.viewmodel.Factory.ProductsViewModelFactory
import com.example.products.viewmodel.ListOfProductsViewModel
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.AppState
import com.example.products.viewmodel.uiState.ProductsUiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ListOfProductsScreen(
    navController: NavController, viewModel: ListOfProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(LocalContext.current)
    )
) {

    val localContext = LocalContext.current

    val appState = viewModel.listOfProducts.collectAsState().value.appState

    val listOfResponseData = viewModel.listOfProducts.collectAsState().value
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    Scaffold(
        Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.updateAllProductsData()
                Toast.makeText(localContext, "Данные обновлены", Toast.LENGTH_SHORT).show()
            },
        ) {
            ScreenBody(
                Modifier.padding(innerPadding),
                navController,
                listOfResponseData,
                viewModel,
                appState,
            )
        }
    }
}

@Composable
private fun ScreenBody(
    modifier: Modifier,
    navigation: NavController,
    listOfResponseData: ProductsUiState,
    viewModel: ListOfProductsViewModel,
    appState: AppState,
) {

    Box(
        Modifier.fillMaxSize()
    ) {
        ListOfProducts(
            listOfResponseData, navigation, viewModel, appState = appState, modifier = modifier
        )
        Column(modifier) {
            SearchRow(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchRow(viewModel: ListOfProductsViewModel) {

    var text by remember { mutableStateOf(viewModel.textFieldValue) }
    val focusManager = LocalFocusManager.current

    val isSearch = ProductManager.filteredState.collectAsState().value.isSearched

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        TextField(
            trailingIcon = {
                if (!isSearch) {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (text.isNotEmpty()) {
                            viewModel.searchItems()
                            focusManager.clearFocus()
                            ProductManager.updateSearchState(true)
                        }

                    }, content = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    })
                } else {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        text = ""
                        ProductManager.updateSearchState(false)
                    }, content = {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    })
                }
            },
            value = text,
            onValueChange = { newText ->
                text = newText
                viewModel.textFieldValue = text
                if (text.isEmpty()) ProductManager.updateSearchState(false)
            },
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surfaceTint,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,

                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),

            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

            keyboardActions = KeyboardActions(onNext = {
                keyboardController?.hide()
                if (text.isNotEmpty()) ProductManager.updateSearchState(true)
                viewModel.searchItems()
            }),

            singleLine = true,
            placeholder = {
                Text(
                    "Введите запрос",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowOfCategories(
    viewModel: ListOfProductsViewModel, listOfResponseData: ProductsUiState
) {
    LazyRow {
        if (listOfResponseData.listCategories != null) {
            items(listOfResponseData.listCategories) { category ->
                val isSelected = viewModel.listOfProducts.value.selectedCategoriesToChipState?.get(
                    category
                ) ?: false
                Spacer(modifier = Modifier.width(8.dp))
                ElevatedFilterChip(
                    onClick = {
                        viewModel.setFilterChipState(!isSelected, category)
                    },
                    elevation = FilterChipDefaults.filterChipElevation(elevation = 0.dp),
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
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
    navigation: NavController,
    viewModel: ListOfProductsViewModel,
    appState: AppState,
    modifier: Modifier
) {
    val productManagerState = ProductManager.filteredState.collectAsState().value
    val isFiltered = productManagerState.isFiltered
    val isSearched = productManagerState.isSearched

    LazyColumn(
        contentPadding = PaddingValues(top = 98.dp)
    ) {
        item {
            RowOfCategories(viewModel = viewModel, listOfResponseData = listOfResponseData)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (appState != AppState.ERROR) {
            val productsList = if (isSearched) {
                listOfResponseData.listSearchProducts
            } else {
                listOfResponseData.listProducts
            }

            productsList?.let { products ->
                items(products) { product ->
                    if (isFiltered && appState == AppState.LOADING) {
                        SkeletonListItem()
                    } else {
                        ProductCard(navigation, product = product)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (appState == AppState.LOADING) {
                    items(20) { SkeletonListItem() }
                }
            } ?: run {
                items(20) { SkeletonListItem() }
            }

            if (!isFiltered && !isSearched && !listOfResponseData.listProducts.isNullOrEmpty()) {
                item { LoadButton(viewModel = viewModel) }
            }
        } else item {
            ErrorText(modifier)
        }
    }
}


@Composable
private fun CustomListItem(
    navigation: NavController,
    product: Product,
    backgroundColor: Color,
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable {
                navigation.navigate(
                    route = "${Screens.ProductScreen.route}/${product.id}",
                ) {
                    popUpTo(Screens.MainScreen.route) {
                        inclusive = false
                    }
                }
            }, color = backgroundColor, shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "$${product.price}",
                    fontFamily = nunitoFontFamily,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = product.title,
                    fontFamily = nunitoFontFamily,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = product.description,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SkeletonListItem() {
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
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.outline,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush),
                )
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush),
                )
            }

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
private fun LoadButton(viewModel: ListOfProductsViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .width(256.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    ProductManager.updateCurrentPage(ProductManager.currentPage.value.currentPage + 1)
                    viewModel.changePage(true)
                }, Alignment.Center
        ) {
            Text(
                text = "Загрузить ещё",
                textAlign = TextAlign.Center,
                fontFamily = nunitoFontFamily,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
    Spacer(modifier = Modifier.height(128.dp))
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

@Composable
private fun ProductCard(
    navController: NavController, product: Product
) {
    CustomListItem(
        navController,
        product = product,
        backgroundColor = MaterialTheme.colorScheme.surface,
    )
}