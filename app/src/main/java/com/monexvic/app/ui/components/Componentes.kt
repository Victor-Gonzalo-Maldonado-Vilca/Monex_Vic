package com.monexvic.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.Menta
import com.monexvic.app.ui.theme.PurpuraApagado
import com.monexvic.app.ui.theme.RojoError
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.monexvic.app.data.local.TipoTransaccion
import com.monexvic.app.data.local.Transaccion
import com.monexvic.app.ui.theme.Blanco
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.MentaOscuro
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.theme.PurpuraOscuro
import com.monexvic.app.ui.theme.PurpuraTexto

/** Barra superior con el logo "MonexVic". */
@Composable
fun EncabezadoApp(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PurpuraOscuro)) { append("Monex") }
                withStyle(SpanStyle(color = Purpura)) { append("Vic") }
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun TituloPantalla(
    texto: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.headlineMedium,
            color = PurpuraOscuro,
            modifier = Modifier.weight(1f),
        )
        accion?.invoke()
    }
}

@Composable
fun IconoCategoria(categoria: String, modifier: Modifier = Modifier, tamano: Dp = 44.dp) {
    val ui = Categorias.ui(categoria)
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(ui.fondo),
        contentAlignment = Alignment.Center,
    ) {
        Icon(ui.icono, contentDescription = null, tint = ui.tinte, modifier = Modifier.size(tamano / 2))
    }
}

/** Fila de una transacción: icono, concepto, categoría y monto con signo. */
@Composable
fun FilaTransaccion(
    transaccion: Transaccion,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val esIngreso = transaccion.tipo == TipoTransaccion.AHORRO
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconoCategoria(transaccion.categoria)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                transaccion.concepto,
                style = MaterialTheme.typography.titleMedium,
                color = PurpuraOscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(transaccion.categoria, style = MaterialTheme.typography.bodyMedium, color = GrisTexto)
        }
        Text(
            text = formatoConSigno(transaccion.monto, transaccion.tipo),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (esIngreso) MentaOscuro else PurpuraTexto,
        )
    }
}

/** Tarjeta blanca redondeada usada en estadísticas. */
@Composable
fun TarjetaBlanca(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(Modifier.padding(18.dp)) { content() }
    }
}

@Composable
fun coloresChip(): SelectableChipColors = FilterChipDefaults.filterChipColors(
    containerColor = Blanco,
    labelColor = PurpuraOscuro,
    iconColor = Purpura,
    selectedContainerColor = Purpura,
    selectedLabelColor = Blanco,
    selectedLeadingIconColor = Blanco,
)

/** Altura común de los botones principales: ni muy grandes ni muy pequeños. */
val AltoBoton = 48.dp
private val FormaBoton = RoundedCornerShape(14.dp)

/** Botón principal de la app (Guardar, Aportar...). */
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Purpura,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBoton,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Blanco),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier.height(AltoBoton),
    ) {
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

/** Botón secundario con borde, del mismo tamaño que [BotonPrimario]. */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBoton,
        border = BorderStroke(1.dp, if (enabled) PurpuraApagado else LavandaMedia),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Purpura),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier.height(AltoBoton),
    ) {
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

/** Botón de texto rojo con icono de papelera, para eliminar. */
@Composable
fun BotonEliminar(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = RojoError),
        modifier = modifier,
    ) {
        Icon(Icons.Outlined.DeleteOutline, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(texto)
    }
}

/** Lugar donde aparecen los avisos con "Deshacer", con el estilo de MonexVic. */
@Composable
fun SnackbarMonexVic(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState, modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { datos ->
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PurpuraOscuro,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Blanco.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = Blanco, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    datos.visuals.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Blanco,
                    modifier = Modifier.weight(1f),
                )
                datos.visuals.actionLabel?.let { accion ->
                    TextButton(
                        onClick = { datos.performAction() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Menta),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(accion, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Muestra "[mensaje] · Deshacer" y llama a [onDeshacer] si el usuario lo toca.
 * Cierra antes el aviso anterior para que no se queden en cola al eliminar varios seguidos.
 */
suspend fun SnackbarHostState.mostrarDeshacer(mensaje: String, onDeshacer: () -> Unit) {
    currentSnackbarData?.dismiss()
    val resultado = showSnackbar(mensaje, actionLabel = "Deshacer", duration = SnackbarDuration.Long)
    if (resultado == SnackbarResult.ActionPerformed) onDeshacer()
}
