package com.monexvic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monexvic.app.ui.components.EncabezadoApp
import com.monexvic.app.ui.components.FilaTransaccion
import com.monexvic.app.ui.components.NuevoMovimientoSheet
import com.monexvic.app.ui.components.TituloPantalla
import com.monexvic.app.ui.components.formatoSoles
import com.monexvic.app.ui.theme.Blanco
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.Menta
import com.monexvic.app.ui.theme.MentaOscuro
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.theme.PurpuraApagado
import com.monexvic.app.ui.theme.PurpuraOscuro
import com.monexvic.app.ui.viewmodel.FinanzasViewModel

@Composable
fun InicioScreen(
    viewModel: FinanzasViewModel,
    onVerMovimientos: () -> Unit,
) {
    val state by viewModel.inicioState.collectAsStateWithLifecycle()
    var mostrarNuevo by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        EncabezadoApp()
        TituloPantalla("Inicio")
        TarjetaBalance(
            balance = state.balance,
            onAgregar = { mostrarNuevo = true },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))
        FilaMetricas(ahorrado = state.ahorradoMes, gastado = state.gastadoMes)
        Spacer(Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Blanco,
            shadowElevation = 2.dp,
        ) {
            Column {
                Box(
                    Modifier
                        .padding(top = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(LavandaMedia)
                        .align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.padding(start = 20.dp, end = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Últimos Movimientos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PurpuraOscuro,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onVerMovimientos) { Text("Ver todo", color = Purpura) }
                }
                if (state.ultimosMovimientos.isEmpty()) {
                    Text(
                        "Aún no tienes movimientos. Toca + para registrar el primero.",
                        color = GrisTexto,
                        modifier = Modifier.padding(20.dp),
                    )
                }
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(state.ultimosMovimientos, key = { it.id }) { FilaTransaccion(it) }
                }
            }
        }
    }

    if (mostrarNuevo) {
        NuevoMovimientoSheet(
            onDismiss = { mostrarNuevo = false },
            onGuardar = { concepto, monto, tipo, categoria ->
                viewModel.agregarTransaccion(concepto, monto, tipo, categoria)
                mostrarNuevo = false
            },
        )
    }
}

@Composable
private fun TarjetaBalance(balance: Double, onAgregar: () -> Unit, modifier: Modifier = Modifier) {
    // El padding inferior deja espacio para el botón (+) que sobresale de la tarjeta
    Box(modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Purpura),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(Modifier.padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 40.dp)) {
                Text("Mi Balance", style = MaterialTheme.typography.titleMedium, color = Blanco.copy(alpha = 0.9f))
                Spacer(Modifier.height(6.dp))
                Text(
                    formatoSoles(balance),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Blanco,
                    maxLines = 1,
                )
            }
        }
        FloatingActionButton(
            onClick = onAgregar,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = 24.dp)
                .size(56.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = Menta,
            contentColor = Blanco,
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nuevo movimiento", modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun FilaMetricas(ahorrado: Double, gastado: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Metrica("Ahorrado este mes", formatoSoles(ahorrado), MentaOscuro, Modifier.weight(1f))
        VerticalDivider(color = LavandaMedia, thickness = 1.dp)
        Metrica("Gastado este mes", formatoSoles(gastado), PurpuraApagado, Modifier.weight(1f))
    }
}

@Composable
private fun Metrica(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(titulo, style = MaterialTheme.typography.bodyMedium, color = PurpuraOscuro, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
    }
}
