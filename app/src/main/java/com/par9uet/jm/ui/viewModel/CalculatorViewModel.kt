package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.store.LocalSettingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

sealed class CalculatorAction {
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    data class Number(val number: Int) : CalculatorAction()
    object Calculate : CalculatorAction()
    object Decimal : CalculatorAction()
}

// 解析后的 token
sealed class Token {
    data class Num(val value: Double) : Token()
    data class Op(val operation: CalculatorOperation) : Token()
}

sealed class CalculatorOperation(val operation: String) {
    object Divide : CalculatorOperation("/")
    object Multiply : CalculatorOperation("X")
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
}

private fun parseTokens(list: List<CalculatorAction>): List<Token> {
    val tokens = mutableListOf<Token>()
    val numberBuffer = StringBuilder()
    var hasDecimal = false

    fun flushNumber() {
        if (numberBuffer.isNotEmpty()) {
            tokens.add(Token.Num(numberBuffer.toString().toDouble()))
            numberBuffer.clear()
            hasDecimal = false
        }
    }

    for (action in list) {
        when (action) {
            is CalculatorAction.Number -> numberBuffer.append(action.number)
            CalculatorAction.Decimal -> {
                if (!hasDecimal) {
                    if (numberBuffer.isEmpty()) numberBuffer.append("0") // ".5" -> "0.5"
                    numberBuffer.append(".")
                    hasDecimal = true
                }
            }

            is CalculatorAction.Operation -> {
                flushNumber()
                tokens.add(Token.Op(action.operation))
            }

            CalculatorAction.Calculate, CalculatorAction.Clear, CalculatorAction.Delete -> {
                // 这几个不应该出现在待求值的列表里
            }
        }
    }
    flushNumber()
    return tokens
}

private fun compute(a: Double, op: CalculatorOperation, b: Double): Double {
    return when (op) {
        CalculatorOperation.Add -> a + b
        CalculatorOperation.Subtract -> a - b
        CalculatorOperation.Multiply -> a * b
        CalculatorOperation.Divide -> if (b == 0.0) 0.0 else a / b
    }
}

private fun evalLeftToRight(tokens: List<Token>): Double {
    var acc = (tokens[0] as Token.Num).value
    var i = 1
    while (i + 1 < tokens.size) {
        val op = (tokens[i] as Token.Op).operation
        val num = (tokens[i + 1] as Token.Num).value
        acc = compute(acc, op, num)
        i += 2
    }
    return acc
}

private fun numberToActions(value: Double): List<CalculatorAction> {
    val str = if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }

    return str.map { c ->
        when (c) {
            '.' -> CalculatorAction.Decimal
            else -> CalculatorAction.Number(c - '0')
        }
    }
}

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val localSettingManager: LocalSettingManager
) : ViewModel() {
    private val _actionList: MutableStateFlow<List<CalculatorAction>> =
        MutableStateFlow(mutableListOf())
    val actionList = _actionList.asStateFlow()
    val actionStr = actionList.map {
        it.joinToString("") { act ->
            when (act) {
                is CalculatorAction.Number -> act.number.toString()
                is CalculatorAction.Decimal -> "."
                is CalculatorAction.Operation -> " " + act.operation.operation + " "
                else -> ""
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun onAction(action: CalculatorAction) {
        when (action) {
            CalculatorAction.Calculate -> calculate()
            CalculatorAction.Delete -> delete()
            CalculatorAction.Clear -> clear()
            CalculatorAction.Decimal -> {
                val list = actionList.value
                // 找当前数字段里是否已有点
                val lastOpIndex = list.indexOfLast { it is CalculatorAction.Operation }
                val currentSegment = list.subList(lastOpIndex + 1, list.size)
                val alreadyHasDecimal = currentSegment.any { it is CalculatorAction.Decimal }
                if (!alreadyHasDecimal) {
                    _actionList.update { it + action }
                }
            }

            is CalculatorAction.Number -> {
                _actionList.update {
                    it + action
                }
            }

            is CalculatorAction.Operation -> {
                onOperation(action.operation)
            }
        }
    }

    private fun onOperation(op: CalculatorOperation) {
        val list = actionList.value
        if (list.isEmpty()) return

        // 结尾是运算符 → 替换
        if (list.last() is CalculatorAction.Operation) {
            _actionList.update { it.dropLast(1) + CalculatorAction.Operation(op) }
            return
        }

        // 结算前面完整的一段
        val tokens = parseTokens(list)
        // tokens 形如 [Num, Op, Num] 或 [Num, Op, Num, Op, Num...]
        // 即时计算：从左到右依次算
        val reducedValue = tokens
            .takeIf { it.size >= 3 && it.last() is Token.Num }
            ?.let { evalLeftToRight(it) }

        val newList = if (reducedValue != null) {
            // 把整个表达式替换成单个结果数字，再追加运算符
            numberToActions(reducedValue) + CalculatorAction.Operation(op)
        } else {
            list + CalculatorAction.Operation(op)
        }
        _actionList.update { newList }
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
        if (actionStr.value == "7279838") {
            localSettingManager.unLockFakeApp()
        }
        _actionList.update {
            mutableListOf()
        }
    }

    private fun calculate() {
        val list = actionList.value
        if (list.isEmpty()) return

        val tokens = parseTokens(list)

        val result: Double = when {
            // 结尾是运算符：2 + =  → 复用左操作数 2+2
            tokens.last() is Token.Op -> {
                val op = (tokens.last() as Token.Op).operation
                val left = (tokens.dropLast(1).lastOrNull() as? Token.Num)?.value ?: return
                compute(left, op, left)
            }
            // 完整表达式
            tokens.size >= 3 -> evalLeftToRight(tokens)
            // 单个数字：2 =  → 2
            tokens.size == 1 -> (tokens[0] as Token.Num).value
            else -> return
        }

        _actionList.update { numberToActions(result) }
    }
}