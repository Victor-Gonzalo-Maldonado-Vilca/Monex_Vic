package com.monexvic.ahorraapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

private val AhorraColorScheme = lightColorScheme(
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
    error = PurpuraTexto,
)

/** Degradado de fondo de las pantallas: lavanda suave hacia el gris muy claro. */
val FondoPantalla = Brush.verticalGradient(listOf(Lavanda, FondoGeneral))

/**
 * Tema de AhorraApp. Se usa siempre el esquema de marca (sin colores dinámicos)
 * para respetar la identidad visual del diseño.
 */
@Composable
fun AhorraAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AhorraColorScheme,
        typography = AhorraTypography,
        content = content,
    )
}
