package com.par9uet.jm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.par9uet.jm.R
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem

@Composable
fun AboutScreen() {
    CommonScaffold(
        title = "关于"
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 100.dp, bottom = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_with_name),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth(.7f)
                            .aspectRatio(837f / 263),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = "jm-mobile",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1.3.0(#5e28dce)",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            item {
                SettingGroup {
                    SettingListItem(
                        icon = Icons.Default.Code,
                        iconContentDescription = "仓库",
                        title = "代码仓库"
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "点击进入"
                        )
                    }
                }
            }
        }
    }
}