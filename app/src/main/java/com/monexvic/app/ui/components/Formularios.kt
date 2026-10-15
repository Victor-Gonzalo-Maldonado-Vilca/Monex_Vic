package com.monexvic.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monexvic.app.data.local.MetaAhorro
import com.monexvic.app.data.local.TipoTransaccion
import com.monexvic.app.ui.theme.Blanco
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.Menta
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.theme.PurpuraOscuro
import com.monexvic.app.ui.theme.PurpuraTexto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates

/** Convierte el texto de un campo de monto ("12,50" o "12.50") en número. */
private fun leerMonto(texto: String): Double? = texto.replace(',', '.').toDoubleOrNull()

private fun soloMonto(texto: String): String = texto.filter { it.isDigit() || it == '.' || it == ',' }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NuevoMovimientoSheet(
    onDismiss: () -> Unit,
    onGuardar: (concepto: String, monto: Double, tipo: TipoTransaccion, categoria: String, fecha: LocalDate) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tipo by rememberSaveable { mutableStateOf(TipoTransaccion.GASTO) }
    var concepto by rememberSaveable { mutableStateOf("") }
    var montoTexto by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable() { mutableStateOf(LocalDate.now() ) }
    var mostrarCalendario by rememberSaveable() { mutableStateOf( false ) }
    val categorias = Categorias.de(tipo)
    var categoria by rememberSaveable(tipo) { mutableStateOf(categorias.first()) }
    val monto = leerMonto(montoTexto)
    val valido = concepto.isNotBlank() && monto != null && monto > 0.0

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Blanco) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Nuevo movimiento", style = MaterialTheme.typography.titleLarge, color = PurpuraOscuro)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoTransaccion.entries.forEach { opcion ->
                    FilterChip(
                        selected = tipo == opcion,
                        onClick = { tipo = opcion },
                        label = { Text(if (opcion == TipoTransaccion.AHORRO) "Ahorro / Ingreso" else "Gasto") },
                        shape = CircleShape,
                        colors = coloresChip(),
                    )
                }
            }

            OutlinedTextField(
                value = concepto,
                onValueChange = { concepto = it },
                label = { Text("Concepto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = montoTexto,
                onValueChange = { montoTexto = soloMonto(it) },
                label = { Text("Monto") },
                prefix = { Text("S/ ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedButton(
                onClick = {
                    mostrarCalendario = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Seleccionar Fecha"
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = etiquetaFecha(fecha),
                    style = MaterialTheme.typography.bodyMedium

                )
            }

            Text("Categoría", style = MaterialTheme.typography.titleMedium, color = PurpuraOscuro)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.forEach { opcion ->
                    FilterChip(
                        selected = categoria == opcion,
                        onClick = { categoria = opcion },
                        label = { Text(opcion) },
                        leadingIcon = {
                            Icon(Categorias.ui(opcion).icono, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        shape = CircleShape,
                        colors = coloresChip(),
                    )
                }
            }

            if (mostrarCalendario) {
                val hoyUtc = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val estado = rememberDatePickerState(
                    initialSelectedDateMillis = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= hoyUtc
                    },
                )
                DatePickerDialog(
                    onDismissRequest = { mostrarCalendario = false },
                    confirmButton = {
                        TextButton(onClick = {
                            estado.selectedDateMillis?.let {
                                fecha = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                            }
                            mostrarCalendario = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } },
                ) {
                    DatePicker(state = estado)
                }
            }

            Button(
                onClick = { if (monto != null) onGuardar(concepto, monto, tipo, categoria, fecha) },
                enabled = valido,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purpura),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Guardar")
            }
        }
    }
}

val IconosMeta = listOf("🎯", "🏝️", "🛡️", "💻", "🚗", "🏠", "🎓", "✈️", "🎁")

@Composable
fun NuevaMetaDialog(
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, objetivo: Double, icono: String) -> Unit,
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var objetivoTexto by rememberSaveable { mutableStateOf("") }
    var icono by rememberSaveable { mutableStateOf(IconosMeta.first()) }
    val objetivo = leerMonto(objetivoTexto)
    val valido = nombre.isNotBlank() && objetivo != null && objetivo > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Blanco,
        title = { Text("Nueva meta de ahorro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = objetivoTexto,
                    onValueChange = { objetivoTexto = soloMonto(it) },
                    label = { Text("Monto objetivo") },
                    prefix = { Text("S/ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    IconosMeta.forEach { opcion ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (opcion == icono) LavandaMedia else Blanco)
                                .clickable { icono = opcion },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(opcion, fontSize = 22.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (objetivo != null) onGuardar(nombre, objetivo, icono) },
                enabled = valido,
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun AportarMetaDialog(
    meta: MetaAhorro,
    onDismiss: () -> Unit,
    onAportar: (monto: Double) -> Unit,
    onEliminar: () -> Unit,
) {
    var montoTexto by rememberSaveable { mutableStateOf("") }
    val monto = leerMonto(montoTexto)
    val valido = monto != null && monto > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Blanco,
        title = { Text("${meta.icono} ${meta.nombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Llevas ${formatoSolesCorto(meta.montoActual)} de ${formatoSolesCorto(meta.montoObjetivo)}",
                    color = GrisTexto,
                )
                OutlinedTextField(
                    value = montoTexto,
                    onValueChange = { montoTexto = soloMonto(it) },
                    label = { Text("Monto") },
                    prefix = { Text("S/ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                TextButton(onClick = onEliminar, contentPadding = ButtonDefaults.TextButtonContentPadding) {
                    Text("Eliminar meta", color = PurpuraTexto)
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = { if (monto != null) onAportar(-monto) }, enabled = valido) {
                    Text("Retirar")
                }
                Button(
                    onClick = { if (monto != null) onAportar(monto) },
                    enabled = valido,
                    colors = ButtonDefaults.buttonColors(containerColor = Menta),
                ) { Text("Aportar") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
