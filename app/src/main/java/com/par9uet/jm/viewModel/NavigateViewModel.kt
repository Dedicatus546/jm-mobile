package com.par9uet.jm.viewModel

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class NavigateViewModel : ViewModel() {
    private var nc: NavController? = null
    private val _currentRoute = MutableStateFlow<String?>(null)
    val currentRoute: StateFlow<String?> = _currentRoute

    fun setNavController(controller: NavController) {
        nc = controller
        nc?.addOnDestinationChangedListener { _, destination, _ ->
            _currentRoute.value = destination.route
        }
    }

    fun navigate(name: String) {
        nc?.navigate(name)
    }

    fun back() {
        nc?.popBackStack()
    }
}

class AppNavigateViewModel : NavigateViewModel()

class TabNavigateViewModel : NavigateViewModel()

@Composable
fun rememberAppNavigateViewModel(): AppNavigateViewModel {
    val context = LocalContext.current as ComponentActivity
    val vm: AppNavigateViewModel = viewModel(context)
    return vm
}

@Composable
fun rememberTabNavigateViewModel(): TabNavigateViewModel {
    val context = LocalContext.current as ComponentActivity
    val vm: TabNavigateViewModel = viewModel(context)
    return vm
}