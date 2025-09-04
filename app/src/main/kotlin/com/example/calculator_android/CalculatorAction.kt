// app/src/main/kotlin/com/example/calculator_android/CalculatorAction.kt
package com.example.calculator_android

sealed class CalculatorAction
data class Number(val number: Int) : CalculatorAction()
object Decimal : CalculatorAction()
object Clear : CalculatorAction()
data class Operation(val operator: String) : CalculatorAction()
object Calculate : CalculatorAction()
object Delete : CalculatorAction()
object Percentage : CalculatorAction()
object Sqrt : CalculatorAction()
object ToggleSign : CalculatorAction()
object MemoryAdd : CalculatorAction()
object MemoryClear : CalculatorAction()
object MemoryRecall : CalculatorAction()