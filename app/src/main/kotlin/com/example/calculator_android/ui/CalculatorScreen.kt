// app/src/main/kotlin/com/example/calculator_android/ui/CalculatorScreen.kt
package com.example.calculator_android.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.rememberScrollState
import com.example.calculator_android.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.state

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = state.subDisplay,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = state.mainDisplay,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "display"
                ) { targetDisplay ->
                    Text(
                        text = targetDisplay,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Light
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (state.steps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.steps,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .verticalScroll(rememberScrollState()),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Memory indicator
        if (state.memoryValue != 0.0) {
            Text(
                text = "M: ${viewModel.formatNumber(state.memoryValue)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        }

        // Button grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("MC", isSpecial = true) { viewModel.onAction(MemoryClear) }
                CalculatorButton("MR", isSpecial = true) { viewModel.onAction(MemoryRecall) }
                CalculatorButton("M+", isSpecial = true) { viewModel.onAction(MemoryAdd) }
                CalculatorButton("C", isOperator = true) { viewModel.onAction(Clear) }
                CalculatorButton("⌫", isOperator = true) { viewModel.onAction(Delete) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("√", isOperator = true) { viewModel.onAction(Sqrt) }
                CalculatorButton("^", isOperator = true) { viewModel.onAction(Operation("^")) }
                CalculatorButton("%", isOperator = true) { viewModel.onAction(Percentage) }
                CalculatorButton("÷", isOperator = true) { viewModel.onAction(Operation("/")) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("7") { viewModel.onAction(Number(7)) }
                CalculatorButton("8") { viewModel.onAction(Number(8)) }
                CalculatorButton("9") { viewModel.onAction(Number(9)) }
                CalculatorButton("×", isOperator = true) { viewModel.onAction(Operation("*")) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("4") { viewModel.onAction(Number(4)) }
                CalculatorButton("5") { viewModel.onAction(Number(5)) }
                CalculatorButton("6") { viewModel.onAction(Number(6)) }
                CalculatorButton("-", isOperator = true) { viewModel.onAction(Operation("-")) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("1") { viewModel.onAction(Number(1)) }
                CalculatorButton("2") { viewModel.onAction(Number(2)) }
                CalculatorButton("3") { viewModel.onAction(Number(3)) }
                CalculatorButton("+", isOperator = true) { viewModel.onAction(Operation("+")) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("±", isOperator = true) { viewModel.onAction(ToggleSign) }
                CalculatorButton("0") { viewModel.onAction(Number(0)) }
                CalculatorButton(".", isOperator = true) { viewModel.onAction(Decimal) }
                CalculatorButton("=", isEquals = true) { viewModel.onAction(Calculate) }
            }
        }
    }
}

@Composable
fun RowScope.CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isEquals: Boolean = false,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    val backgroundColor = when {
        isEquals -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.secondary
        isSpecial -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isEquals -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.onSecondary
        isSpecial -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier
            .weight(1f)
            .height(70.dp)
            .scale(scale)
            .shadow(if (isPressed) 2.dp else 6.dp, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val duration = if (isEquals) 100L else 50L
                        if (vibrator != null && vibrator.hasVibrator()) {
                            val effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                            vibrator.vibrate(effect)
                        }
                        tryAwaitRelease()
                        onClick()
                    }
                )
            }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                color = contentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}