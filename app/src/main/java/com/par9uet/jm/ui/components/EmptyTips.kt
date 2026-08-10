package com.par9uet.jm.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.par9uet.jm.R

@Composable
fun EmptyTips(
    modifier: Modifier = Modifier,
    emptyMsg: String? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_tip),
            contentDescription = "无数据",
            modifier = Modifier.fillMaxWidth(.3f)
        )
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = emptyMsg ?: "来到了无人的荒原",
            maxLines = 1,
        )
    }
}