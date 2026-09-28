package com.par9uet.jm.ui.compsable

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.par9uet.jm.ui.provider.LocalLocalSettingManager

private val aliasAppNames = listOf(
    "JM",
    "Calculator"
)

private fun switchAlias(targetAliasName: String, context: Context) {
    val pm = context.packageManager
    aliasAppNames.forEach { name ->
        val packageName = context.packageName
        val componentName = ComponentName(packageName, "$packageName.$name")
        val newState = if (name == targetAliasName) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        // DONT_KILL_APP 切换时不要杀掉应用进程
        pm.setComponentEnabledSetting(
            componentName,
            newState,
            PackageManager.DONT_KILL_APP
        )
    }
}

@Composable
fun FakeAppIconEffect() {
    val localSettingManager = LocalLocalSettingManager.current
    val context = LocalContext.current
    val activity = LocalActivity.current

    val localSetting by localSettingManager.localSettingState.collectAsState()

    LaunchedEffect(localSetting.enableFakeAppIcon) {
        val currentAliasName = activity?.componentName?.className ?: ""
        if (localSetting.enableFakeAppIcon && !currentAliasName.contains("Calculator")) {
            switchAlias("Calculator", context)
        } else if(!localSetting.enableFakeAppIcon && !currentAliasName.contains("JM")) {
            switchAlias("JM", context)
        }
    }
}