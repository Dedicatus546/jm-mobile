package com.par9uet.jm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IndexNavigateViewModel() : ViewModel() {
    var nc: NavController? = null
    private val _currentRoute = MutableStateFlow<String?>(null)
    val currentRoute: StateFlow<String?> = _currentRoute

    fun setNavController(controller: NavController) {
        nc = controller
        nc?.addOnDestinationChangedListener { _, destination, _ ->
            _currentRoute.value = destination.route
        }
        Log.v("navigate", "setNavController $nc")
    }

    fun navigate(name: String) {
        nc?.navigate(name)
    }

    fun back() {
        nc?.popBackStack()
    }
}