package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxSize(),
        onClick = {

        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                fontSize = 24.sp
            )
        }
    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun Calculator() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "计算器",
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    text = "2x3",
                    textAlign = TextAlign.End,
                    fontSize = 40.sp
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Grid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f),
                config = {
                    repeat(4) {
                        column(1.fr)
                    }
                    repeat(5) {
                        row(1.fr)
                    }
                    columnGap(16.dp)
                    rowGap(16.dp)
                }
            ) {
                CalculatorButton("C", modifier = Modifier.gridItem(columnSpan = 2))
                CalculatorButton("D")
                CalculatorButton("/")

                CalculatorButton("7")
                CalculatorButton("8")
                CalculatorButton("9")
                CalculatorButton("X")

                CalculatorButton("4")
                CalculatorButton("5")
                CalculatorButton("6")
                CalculatorButton("-")

                CalculatorButton("1")
                CalculatorButton("2")
                CalculatorButton("3")
                CalculatorButton("+")

                CalculatorButton("0", modifier = Modifier.gridItem(columnSpan = 2))
                CalculatorButton(".")
                CalculatorButton("=")
            }
        }
    }
}