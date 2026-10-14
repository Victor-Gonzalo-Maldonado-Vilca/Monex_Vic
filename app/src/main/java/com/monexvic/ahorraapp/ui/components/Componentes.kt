package com.monexvic.ahorraapp.ui.components

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
import androidx.compose.material.icons.outlined.Notifications
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
import com.monexvic.ahorraapp.data.local.TipoTransaccion
import com.monexvic.ahorraapp.data.local.Transaccion
import com.monexvic.ahorraapp.ui.theme.Blanco
import com.monexvic.ahorraapp.ui.theme.GrisTexto
import com.monexvic.ahorraapp.ui.theme.MentaOscuro
import com.monexvic.ahorraapp.ui.theme.Purpura
import com.monexvic.ahorraapp.ui.theme.PurpuraOscuro
import com.monexvic.ahorraapp.ui.theme.PurpuraTexto

/** Barra superior con el logo "MonexVic" y la campana de notificaciones. */
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
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notificaciones",
            tint = PurpuraOscuro,
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
            style = MaterialTheme.typography.headlineLarge,
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
