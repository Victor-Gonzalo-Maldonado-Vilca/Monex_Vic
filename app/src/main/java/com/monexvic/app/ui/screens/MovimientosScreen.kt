package com.monexvic.app.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monexvic.app.data.local.Transaccion
import com.monexvic.app.ui.components.EncabezadoApp
import com.monexvic.app.ui.components.FilaTransaccion
import com.monexvic.app.ui.components.NuevoMovimientoSheet
import com.monexvic.app.ui.components.SnackbarMonexVic
import com.monexvic.app.ui.components.TituloPantalla
import com.monexvic.app.ui.components.mostrarDeshacer
import com.monexvic.app.ui.components.coloresChip
import com.monexvic.app.ui.components.etiquetaFecha
import com.monexvic.app.ui.components.formatoConSigno
import com.monexvic.app.ui.theme.Blanco
import com.monexvic.app.ui.theme.GrisBusqueda
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.Menta
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.theme.RojoError
import com.monexvic.app.ui.theme.PurpuraApagado
import com.monexvic.app.ui.theme.PurpuraOscuro
import com.monexvic.app.ui.viewmodel.FiltroMovimiento
import com.monexvic.app.ui.viewmodel.FinanzasViewModel
import kotlinx.coroutines.launch

@Composable
fun MovimientosScreen(viewModel: FinanzasViewModel) {
    val state by viewModel.movimientosState.collectAsStateWithLifecycle()
    var aEliminar by remember { mutableStateOf<Transaccion?>(null) }
    var aEditar by remember { mutableStateOf<Transaccion?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            EncabezadoApp()
            TituloPantalla("Movimientos")

            TextField(
                value = state.busqueda,
                onValueChange = viewModel::onBusquedaChange,
                placeholder = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrisBusqueda,
                    unfocusedContainerColor = GrisBusqueda,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FiltroMovimiento.entries.forEach { filtro ->
                    val seleccionado = state.filtro == filtro
                    FilterChip(
                        selected = seleccionado,
                        onClick = { viewModel.onFiltroChange(filtro) },
                        label = { Text(filtro.etiqueta) },
                        shape = CircleShape,
                        colors = coloresChip(),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionado,
                            borderColor = if (filtro == FiltroMovimiento.AHORROS) Menta else PurpuraApagado,
                            selectedBorderColor = Purpura,
                            borderWidth = 1.5.dp,
                        ),
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Blanco,
            ) {
                if (state.grupos.isEmpty()) {
                    Text(
                        "No hay movimientos que coincidan.",
                        color = GrisTexto,
                        modifier = Modifier.padding(24.dp),
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp
                    )
                ) {
                    state.grupos.forEach { grupo ->
                        item(key = "fecha-${grupo.fecha}") {
                            Text(
                                etiquetaFecha(grupo.fecha),
                                style = MaterialTheme.typography.labelMedium,
                                color = GrisTexto,
                                modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 2.dp),
                            )
                        }
                        items(grupo.movimientos, key = { it.id }) { transaccion ->
                            FilaTransaccion(transaccion, onClick = { aEditar = transaccion })
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
        SnackbarMonexVic(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
    aEditar?.let { transaccion ->
        NuevoMovimientoSheet(
            inicial = transaccion,
            onDismiss = { aEditar = null },
            onGuardar = { nuevoConcepto, nuevoMonto, nuevoTipo, nuevaCategoria, nuevaFecha ->
                viewModel.actualizarTransaccion(
                    transaccion,
                    concepto = nuevoConcepto,
                    monto = nuevoMonto,
                    tipo = nuevoTipo,
                    categoria = nuevaCategoria,
                    fecha = nuevaFecha
                    )
                aEditar = null
            },
            onEliminar = {
                aEditar = null
                aEliminar = transaccion
            },
        )
    }
    aEliminar?.let { transaccion ->
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            containerColor = Blanco,
            title = { Text("¿Eliminar movimiento?") },
            text = {
                Text(
                    "${transaccion.concepto} · ${
                        formatoConSigno(
                            transaccion.monto, transaccion.tipo
                        )
                    }"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarTransaccion(transaccion)
                    scope.launch {
                        snackbarHostState.mostrarDeshacer("Movimiento eliminado") {
                            viewModel.restaurarTransaccion(transaccion)
                        }
                    }
                    aEliminar = null
                }) { Text("Eliminar", color = RojoError) }
            },
            dismissButton = { TextButton(onClick = { aEliminar = null }) { Text("Cancelar") } },
        )
    }
}
