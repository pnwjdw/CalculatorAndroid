// app/src/main/kotlin/com/example/calculator_android/CalculatorViewModel.kt
package com.example.calculator_android

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.lang.ArithmeticException
import kotlin.math.pow
import kotlin.math.sqrt

class CalculatorViewModel : ViewModel() {
    private val _state = mutableStateOf(CalculatorState())
    val state: State<CalculatorState> = _state

    companion object {
        private const val MAX_NUM_LENGTH = 15
    }

    fun onAction(action: CalculatorAction) {
        val state = _state.value
        if (state.isError && action !is Clear) {
            _state.value = CalculatorState()
            return
        }
        
        when (action) {
            is Number -> enterNumber(action.number)
            Decimal -> enterDecimal()
            Clear -> clear()
            is Operation -> enterOperation(action.operator)
            Calculate -> calculate()
            Delete -> delete()
            Percentage -> percentage()
            Sqrt -> sqrtAction()
            ToggleSign -> toggleSign()
            MemoryAdd -> memoryAdd()
            MemoryClear -> memoryClear()
            MemoryRecall -> memoryRecall()
        }
    }

    private fun enterNumber(number: Int) {
        val state = _state.value
        var display = state.mainDisplay
        if (display == "Error") display = ""
        if (display.length >= MAX_NUM_LENGTH) return
        if (display == "0" || state.isNew) {
            display = number.toString()
        } else {
            display += number.toString()
        }
        _state.value = state.copy(mainDisplay = display, isNew = false, isError = false, steps = "")
    }

    private fun enterDecimal() {
        val state = _state.value
        var display = state.mainDisplay
        if (display == "Error") display = "0"
        if (!display.contains(".")) {
            display += if (display.isEmpty() || display == "0") "0." else "."
        }
        _state.value = state.copy(mainDisplay = display, isNew = false, isError = false, steps = "")
    }

    private fun delete() {
        val state = _state.value
        var display = state.mainDisplay
        if (display == "Error") display = "0"
        if (display.isNotEmpty() && display != "0") {
            display = display.dropLast(1)
            if (display.isEmpty() || display == "-") display = "0"
        }
        _state.value = state.copy(mainDisplay = display, isError = false, steps = "")
    }

    private fun clear() {
        _state.value = CalculatorState()
    }

    private fun enterOperation(op: String) {
        val state = _state.value
        if (state.mainDisplay == "Error") return
        
        var newSub = state.subDisplay
        if (state.isNew && newSub.isNotEmpty()) {
            // Replace last operator
            newSub = newSub.dropLast(3) + " $op "
        } else {
            newSub += state.mainDisplay + " $op "
        }
        _state.value = state.copy(subDisplay = newSub, mainDisplay = "0", isNew = true, steps = "")
    }

    private fun calculate() {
        val state = _state.value
        if (state.mainDisplay == "Error") return
        
        var exp = state.subDisplay + state.mainDisplay
        if (exp.isEmpty()) return
        
        try {
            val tokens = tokenize(exp)
            val rpn = toRPN(tokens)
            val (result, stepsList) = evaluateRPN(rpn)
            val stepsStr = stepsList.joinToString("\n")
            _state.value = state.copy(
                mainDisplay = formatNumber(result),
                subDisplay = "",
                steps = stepsStr,
                isNew = true,
                isError = false
            )
        } catch (e: Exception) {
            _state.value = state.copy(mainDisplay = "Error", isError = true)
        }
    }

    private fun percentage() {
        val state = _state.value
        if (state.mainDisplay == "0" || state.mainDisplay == "Error") return
        val current = state.mainDisplay.toDoubleOrNull() ?: return
        
        val result = if (state.subDisplay.isEmpty()) {
            current / 100
        } else {
            val trimmedSub = state.subDisplay.trim()
            val lastChar = trimmedSub.lastOrNull()
            if (lastChar == '+' || lastChar == '-') {
                val prevExp = trimmedSub.substring(0, trimmedSub.lastIndexOf(lastChar)).trim()
                val previous = if (prevExp.isEmpty()) 0.0 else evaluateExpression(prevExp)
                previous * (current / 100)
            } else {
                current / 100
            }
        }
        
        _state.value = state.copy(mainDisplay = formatNumber(result), isNew = true, steps = "")
    }

    private fun sqrtAction() {
        val state = _state.value
        if (state.mainDisplay == "Error") return
        val current = state.mainDisplay.toDoubleOrNull() ?: return
        
        if (current < 0) {
            _state.value = state.copy(mainDisplay = "Error", isError = true)
            return
        }
        
        val result = sqrt(current)
        _state.value = state.copy(mainDisplay = formatNumber(result), isNew = true, steps = "")
    }

    private fun toggleSign() {
        val state = _state.value
        var display = state.mainDisplay
        if (display == "Error") return
        
        if (display != "0") {
            display = if (display.startsWith("-")) {
                display.drop(1)
            } else {
                "-$display"
            }
        }
        _state.value = state.copy(mainDisplay = display, steps = "")
    }
    
    private fun memoryAdd() {
        val state = _state.value
        val current = state.mainDisplay.toDoubleOrNull() ?: return
        _state.value = state.copy(memoryValue = state.memoryValue + current)
    }
    
    private fun memoryClear() {
        val state = _state.value
        _state.value = state.copy(memoryValue = 0.0)
    }
    
    private fun memoryRecall() {
        val state = _state.value
        _state.value = state.copy(mainDisplay = formatNumber(state.memoryValue), isNew = true, steps = "")
    }

    fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            number.toLong().toString()
        } else {
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }
    
    private fun evaluateExpression(exp: String): Double {
        val tokens = tokenize(exp)
        val rpn = toRPN(tokens)
        return evaluateRPN(rpn).first
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            if (c.isWhitespace()) {
                i++
                continue
            }
            if (c.isDigit() || c == '.' || (c == '-' && (i == 0 || expression[i - 1] in " +-*/^"))) {
                val sb = StringBuilder()
                if (c == '-') {
                    sb.append('-')
                    i++
                }
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    sb.append(expression[i])
                    i++
                }
                tokens.add(sb.toString())
                continue
            }
            if (c in "+-*/^") {
                tokens.add(c.toString())
                i++
                continue
            }
            throw IllegalArgumentException("Invalid token $c")
        }
        return tokens
    }

    private fun toRPN(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operatorStack = mutableListOf<String>()
        for (token in tokens) {
            if (token.toDoubleOrNull() != null) {
                output.add(token)
            } else {
                while (operatorStack.isNotEmpty() &&
                    (precedence(operatorStack.last()) > precedence(token) ||
                            (precedence(operatorStack.last()) == precedence(token) && leftAssoc(token)))
                ) {
                    output.add(operatorStack.removeLast())
                }
                operatorStack.add(token)
            }
        }
        while (operatorStack.isNotEmpty()) {
            output.add(operatorStack.removeLast())
        }
        return output
    }

    private fun evaluateRPN(rpn: List<String>): Pair<Double, List<String>> {
        val stack = mutableListOf<Double>()
        val steps = mutableListOf<String>()
        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.add(num)
            } else {
                val b = stack.removeLast()
                val a = stack.removeLast()
                val res = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) throw ArithmeticException("Division by zero")
                        a / b
                    }
                    "^" -> a.pow(b)
                    else -> throw IllegalArgumentException("Invalid operator")
                }
                val opDesc = when (token) {
                    "+" -> "add"
                    "-" -> "subtract"
                    "*" -> "multiply"
                    "/" -> "divide"
                    "^" -> "raise to power"
                    else -> token
                }
                steps.add("Perform $opDesc: ${formatNumber(a)} $token ${formatNumber(b)} = ${formatNumber(res)}")
                stack.add(res)
            }
        }
        return stack.last() to steps
    }

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/" -> 2
        "^" -> 3
        else -> 0
    }

    private fun leftAssoc(op: String): Boolean = op != "^"
}