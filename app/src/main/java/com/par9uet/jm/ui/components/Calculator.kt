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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.ui.viewModel.CalculatorAction
import com.par9uet.jm.ui.viewModel.CalculatorOperation
import com.par9uet.jm.ui.viewModel.CalculatorViewModel

@Composable
private fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxSize(),
        onClick = onClick
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
    val calculatorViewModel: CalculatorViewModel = hiltViewModel()
    val actionStr by calculatorViewModel.actionStr.collectAsState()
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
                    text = actionStr,
                    textAlign = TextAlign.End,
                    fontSize = 50.sp,
                    lineHeight = 60.sp
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
                CalculatorButton(
                    text = "C",
                    modifier = Modifier.gridItem(columnSpan = 2),
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Clear)
                    }
                )
                CalculatorButton(
                    text = "D",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Delete)
                    }
                )
                CalculatorButton(
                    text = "/",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
                    }
                )
                CalculatorButton(
                    text = "7",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(7))
                    }
                )
                CalculatorButton(
                    text = "8",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(8))
                    }
                )
                CalculatorButton(
                    text = "9",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(9))
                    }
                )
                CalculatorButton(
                    text = "X",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
                    }
                )
                CalculatorButton(
                    text = "4",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(4))
                    }
                )
                CalculatorButton(
                    text = "5",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(5))
                    }
                )
                CalculatorButton(
                    text = "6",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(6))
                    }
                )
                CalculatorButton(
                    text = "-",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
                    }
                )
                CalculatorButton(
                    text = "1",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(1))
                    }
                )
                CalculatorButton(
                    text = "2",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(2))
                    }
                )
                CalculatorButton(
                    text = "3",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(3))
                    }
                )
                CalculatorButton(
                    text = "+",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
                    }
                )
                CalculatorButton(
                    text = "0",
                    modifier = Modifier.gridItem(columnSpan = 2),
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Number(0))
                    }
                )
                CalculatorButton(
                    text = ".",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Decimal)
                    }
                )
                CalculatorButton(
                    text = "=",
                    onClick = {
                        calculatorViewModel.onAction(CalculatorAction.Calculate)
                    }
                )
            }
        }
    }
}