// app/src/main/kotlin/com/example/calculator_android/CalculatorState.kt
package com.example.calculator_android

data class CalculatorState(
    val mainDisplay: String = "0",
    val subDisplay: String = "",
    val steps: String = "",
    val isNew: Boolean = true,
    val isError: Boolean = false,
    val memoryValue: Double = 0.0
)