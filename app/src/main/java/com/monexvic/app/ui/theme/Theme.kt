package com.monexvic.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val MonexVicColorScheme = lightColorScheme(
    primary = Purpura,
    onPrimary = Blanco,
    primaryContainer = Lavanda,
    onPrimaryContainer = PurpuraOscuro,
    secondary = Menta,
    onSecondary = Blanco,
    secondaryContainer = MentaClara,
    onSecondaryContainer = PurpuraOscuro,
    tertiary = PurpuraApagado,
    onTertiary = Blanco,
    tertiaryContainer = LavandaMedia,
    onTertiaryContainer = PurpuraOscuro,
    background = FondoGeneral,
    onBackground = PurpuraOscuro,
    surface = Blanco,
    onSurface = PurpuraOscuro,
    surfaceVariant = GrisBusqueda,
    onSurfaceVariant = GrisTexto,
    surfaceContainer = Blanco,
    outline = PurpuraApagado,
    error = RojoError,
)

/** Esquinas redondeadas comunes: campos de texto (12), tarjetas (16-24) y hojas (28). */
private val MonexVicShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Degradado de fondo de las pantallas: lavanda suave hacia el gris muy claro. */
val FondoPantalla = Brush.verticalGradient(listOf(Lavanda, FondoGeneral))

/**
 * Tema de MonexVic. Se usa siempre el esquema de marca (sin colores dinámicos)
 * para respetar la identidad visual del diseño.
 */
@Composable
fun MonexVicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MonexVicColorScheme,
        typography = MonexVicTypography,
        shapes = MonexVicShapes,
        content = content,
    )
}
