package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class CalculatorAction {
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    data class Number(val number: Int) : CalculatorAction()
    object Calculate : CalculatorAction()
    object Decimal : CalculatorAction()
}

sealed class CalculatorOperation(val operation: String) {
    object Divide : CalculatorOperation("/")
    object Multiply : CalculatorOperation("X")
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
}

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {
    private val _actionList: MutableStateFlow<List<CalculatorAction>> =
        MutableStateFlow(mutableListOf())
    val actionList = _actionList.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            CalculatorAction.Calculate -> calculate()
            CalculatorAction.Delete -> delete()
            CalculatorAction.Clear -> clear()
            CalculatorAction.Decimal, is CalculatorAction.Number -> {
                _actionList.update {
                    it.plus(action)
                }
            }

            is CalculatorAction.Operation -> {
                onOperation(action.operation)
            }
        }
    }

    private fun onOperation(op: CalculatorOperation) {
        val list = actionList.value

        if (list.isEmpty()) {
            // 开头就按运算符，忽略（或按需处理负号）
            return
        }

        // 如果最后一项已经是运算符，替换它（用户改主意了）
        if (list.last() is CalculatorAction.Operation) {
            _actionList.update { it.dropLast(1) + CalculatorAction.Operation(op) }
            return
        }

        // 关键：如果列表里已经能凑出 "数字 运算符 数字"，先结算它
        val reduced = reduceIfComplete(list)

        // 追加新运算符
        _actionList.update { reduced + CalculatorAction.Operation(op) }
    }

    // 如果最后是完整的 [..., num, op, num]，就地算出结果替换
    private fun reduceIfComplete(list: List<CalculatorAction>): List<CalculatorAction> {
        // 从后往前找，看最后三/四项是否是 "数字 运算符 数字"
        if (list.size < 3) return list

        val last = list[list.size - 1]
        val op = list[list.size - 2]
        val first = list[list.size - 3]

        if (last is CalculatorAction.Number &&
            op is CalculatorAction.Operation &&
            first is CalculatorAction.Number
        ) {
            val result = compute(first.number.toDouble(), op.operation, last.number.toDouble())
            // 用结果替换掉最后三项
            return list.dropLast(3) + CalculatorAction.Number(result.toInt())
        }
        return list
    }

    private fun compute(a: Double, op: CalculatorOperation, b: Double): Double {
        return when (op) {
            CalculatorOperation.Add -> a + b
            CalculatorOperation.Subtract -> a - b
            CalculatorOperation.Multiply -> a * b
            CalculatorOperation.Divide -> if (b == 0.0) 0.0 else a / b
        }
    }

    private fun delete() {
        val list = actionList.value
        if (list.isNotEmpty()) {
            _actionList.update {
                list.dropLast(1)
            }
        }
    }

    private fun clear() {
        _actionList.update {
            mutableListOf()
        }
    }

    private fun calculate() {

    }
}