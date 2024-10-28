package com.par9uet.jm.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.par9uet.jm.data.models.BottomNavigationRoute
import com.par9uet.jm.viewModel.MainNavigateViewModel
import com.par9uet.jm.viewModel.UserViewModel

@Composable
fun BottomNavigationBarComponent() {
    val mainNavigateViewModel: MainNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    val currentRoute by mainNavigateViewModel.currentRoute.collectAsState()
    val userViewModel: UserViewModel = viewModel(LocalContext.current as ComponentActivity)
    if (currentRoute != "login") {
        NavigationBar {
            BottomNavigationRoute.entries.forEachIndexed { index, item ->
                if (item.show) {
                    key(item.value) {
                        if (item.value != "person" || userViewModel.isLogin) {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        if (currentRoute == item.value) item.selectIcon else item.unSelectIcon,
                                        contentDescription = item.label
                                    )
                                },
                                selected = currentRoute == item.value,
                                onClick = {
                                    mainNavigateViewModel.navigate(item.value)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}