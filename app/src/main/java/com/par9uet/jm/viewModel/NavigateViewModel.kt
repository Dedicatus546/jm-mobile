package com.par9uet.jm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class NavigateViewModel() : ViewModel() {
    private var navController: NavController? = null

    init {
        Log.d("navigate", "$this")
    }

    fun setNavController(controller: NavController) {
        navController = controller
        Log.v("navigate", "setNavController $navController")
    }

    fun navigate(route: String) {
        Log.v("navigate", "navigate $navController $route")
        navController?.navigate(route)
    }
}