package com.monexvic.ahorraapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monexvic.ahorraapp.ui.components.AnilloPorcentaje
import com.monexvic.ahorraapp.ui.components.EncabezadoApp
import com.monexvic.ahorraapp.ui.components.GraficoBarras
import com.monexvic.ahorraapp.ui.components.GraficoDona
import com.monexvic.ahorraapp.ui.components.Leyenda
import com.monexvic.ahorraapp.ui.components.TarjetaBlanca
import com.monexvic.ahorraapp.ui.components.TituloPantalla
import com.monexvic.ahorraapp.ui.components.etiquetaMes
import com.monexvic.ahorraapp.ui.components.formatoSoles
import com.monexvic.ahorraapp.ui.components.nombreMes
import com.monexvic.ahorraapp.ui.theme.ColoresGrafico
import com.monexvic.ahorraapp.ui.theme.GrisTexto
import com.monexvic.ahorraapp.ui.theme.Menta
import com.monexvic.ahorraapp.ui.theme.MentaOscuro
import com.monexvic.ahorraapp.ui.theme.Purpura
import com.monexvic.ahorraapp.ui.theme.PurpuraOscuro
import com.monexvic.ahorraapp.ui.theme.PurpuraTexto
import com.monexvic.ahorraapp.ui.viewmodel.FinanzasViewModel
import java.time.YearMonth

@Composable
fun EstadisticasScreen(viewModel: FinanzasViewModel) {
    val state by viewModel.estadisticasState.collectAsStateWithLifecycle()
    val categorias = state.gastosPorCategoria
    val leyendaCategorias = categorias.mapIndexed { i, c -> c.categoria to ColoresGrafico[i % ColoresGrafico.size] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        EncabezadoApp()
        TituloPantalla("Estadísticas")

        // 1. Gastos del mes por categoría: barras + dona
        TarjetaBlanca {
            Column {
                Subtitulo("Gastos de ${nombreMes(YearMonth.now())}")
                Spacer(Modifier.height(12.dp))
                if (categorias.isEmpty()) {
                    Text("Aún no registras gastos este mes.", color = GrisTexto)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GraficoBarras(
                            valores = categorias.map { it.total.toFloat() },
                            colores = ColoresGrafico,
                            modifier = Modifier.weight(1.4f),
                            alto = 130.dp,
                        )
                        Spacer(Modifier.width(12.dp))
                        GraficoDona(
                            fracciones = categorias.map { it.porcentaje },
                            colores = ColoresGrafico,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Leyenda(leyendaCategorias)
                }
            }
        }

        // 2. Ahorro (ingresos - gastos) de los últimos 6 meses
        TarjetaBlanca {
            Column {
                Subtitulo("Ahorro mensual")
                Spacer(Modifier.height(12.dp))
                GraficoBarras(
                    valores = state.ultimosMeses.map { it.ahorro.coerceAtLeast(0.0).toFloat() },
                    colores = listOf(Menta),
                    etiquetas = state.ultimosMeses.map { etiquetaMes(it.mes) },
                    alto = 160.dp,
                )
            }
        }

        // 3. Mayor categoría de gasto
        state.mayorCategoria?.let { mayor ->
            TarjetaBlanca {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Mayor categoría de gasto:", color = PurpuraOscuro)
                        Text(
                            mayor.categoria,
                            style = MaterialTheme.typography.headlineMedium,
                            color = PurpuraOscuro,
                        )
                        Text(formatoSoles(mayor.total), color = GrisTexto)
                    }
                    AnilloPorcentaje(fraccion = mayor.porcentaje, color = Purpura)
                }
            }
        }

        // 4. Porcentaje de ahorro del mes
        TarjetaBlanca {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Subtitulo("Porcentaje de ahorro")
                    Text("Ingresos: ${formatoSoles(state.ingresosMes)}", color = MentaOscuro)
                    Text("Gastos: ${formatoSoles(state.gastosMes)}", color = PurpuraTexto)
                }
                AnilloPorcentaje(fraccion = state.porcentajeAhorro, color = Menta)
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun Subtitulo(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = PurpuraOscuro,
        modifier = Modifier.padding(bottom = 2.dp),
    )
}
