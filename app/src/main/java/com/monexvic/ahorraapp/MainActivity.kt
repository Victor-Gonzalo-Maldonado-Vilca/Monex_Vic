package com.monexvic.ahorraapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.monexvic.ahorraapp.ui.navigation.MonexVicRaiz
import com.monexvic.ahorraapp.ui.theme.MonexVicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Iconos oscuros en las barras del sistema: el diseño siempre es claro
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            MonexVicTheme {
                MonexVicRaiz()
            }
        }
    }
}
