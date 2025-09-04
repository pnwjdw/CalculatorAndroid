// app/src/main/kotlin/com/example/calculator_android/MainActivity.kt
package com.example.calculator_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculator_android.ui.CalculatorScreen
import com.example.calculator_android.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val themeType = remember { mutableStateOf(ThemeType.DARK) }
            
            MaterialTheme(colorScheme = getColorScheme(themeType.value)) {
                NavHost(
                    navController = navController,
                    startDestination = "calculator",
                    modifier = Modifier
                ) {
                    composable(
                        "calculator",
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() }
                    ) {
                        CalculatorScreen(
                            modifier = Modifier,
                            onSettingsClick = { navController.navigate("settings") }
                        )
                    }
                    composable(
                        "settings",
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() }
                    ) {
                        SettingsScreen(
                            currentTheme = themeType.value,
                            onThemeChange = { themeType.value = it },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}