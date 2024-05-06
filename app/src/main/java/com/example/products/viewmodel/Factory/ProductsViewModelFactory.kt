package com.example.products.viewmodel.Factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.products.viewmodel.ListOfProductsViewModel

class ProductsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListOfProductsViewModel::class.java)) {
            return ListOfProductsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
