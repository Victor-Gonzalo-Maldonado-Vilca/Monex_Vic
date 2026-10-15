package com.monexvic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monexvic.app.data.local.MetaAhorro
import com.monexvic.app.ui.components.AportarMetaDialog
import com.monexvic.app.ui.components.EncabezadoApp
import com.monexvic.app.ui.components.NuevaMetaDialog
import com.monexvic.app.ui.components.SnackbarMonexVic
import com.monexvic.app.ui.components.TituloPantalla
import com.monexvic.app.ui.components.formatoSoles
import com.monexvic.app.ui.components.formatoSolesCorto
import com.monexvic.app.ui.components.mostrarDeshacer
import com.monexvic.app.ui.theme.Blanco
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.Lavanda
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.Menta
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.theme.PurpuraApagado
import com.monexvic.app.ui.theme.PurpuraOscuro
import com.monexvic.app.ui.viewmodel.FinanzasViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MetasScreen(viewModel: FinanzasViewModel) {
    val state by viewModel.metasState.collectAsStateWithLifecycle()
    var mostrarNueva by rememberSaveable { mutableStateOf(false) }
    var seleccionada by remember { mutableStateOf<MetaAhorro?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            EncabezadoApp()
            TituloPantalla("Metas")
            Text(
                "Total en metas: ${formatoSoles(state.totalAhorradoEnMetas)}",
                color = GrisTexto,
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
            )
            if (state.metas.isEmpty()) {
                Text(
                    "Aún no tienes metas. Toca + para crear tu primera meta de ahorro.",
                    color = GrisTexto,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 48.dp),
                )
            }
            LazyColumn(
                // El espacio inferior evita que el botón + tape la última meta
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(state.metas, key = { _, meta -> meta.id }) { indice, meta ->
                    TarjetaMeta(meta, oscura = indice % 2 == 1, onClick = { seleccionada = meta })
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarNueva = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = Purpura,
            contentColor = Blanco,
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nueva meta")
        }

        // Se muestra por encima del botón + para no taparlo
        SnackbarMonexVic(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp),
        )
    }

    if (mostrarNueva) {
        NuevaMetaDialog(
            onDismiss = { mostrarNueva = false },
            onGuardar = { nombre, objetivo, icono ->
                viewModel.agregarMeta(nombre, objetivo, icono)
                mostrarNueva = false
            },
        )
    }

    seleccionada?.let { meta ->
        AportarMetaDialog(
            meta = meta,
            disponible = state.disponible,
            onDismiss = { seleccionada = null },
            onAportar = { monto ->
                viewModel.aportarAMeta(meta, monto)
                seleccionada = null
            },
            onEliminar = {
                viewModel.eliminarMeta(meta)
                seleccionada = null
                scope.launch {
                    snackbarHostState.mostrarDeshacer("Meta \"${meta.nombre}\" eliminada") {
                        viewModel.restaurarMeta(meta)
                    }
                }
            },
        )
    }
}

@Composable
private fun TarjetaMeta(meta: MetaAhorro, oscura: Boolean, onClick: () -> Unit) {
    val fondo = if (oscura) PurpuraApagado else LavandaMedia
    val colorTexto = if (oscura) Blanco else PurpuraOscuro

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    meta.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorTexto,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (oscura) Lavanda else Blanco.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(meta.icono, fontSize = 24.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { meta.progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = Menta,
                trackColor = Blanco,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${formatoSolesCorto(meta.montoActual)} de ${formatoSolesCorto(meta.montoObjetivo)}",
                    color = colorTexto,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "${(meta.progreso * 100).roundToInt()}%",
                    fontWeight = FontWeight.Bold,
                    color = colorTexto,
                )
            }
        }
    }
}
