package com.monexvic.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.PurpuraOscuro
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/** Redondea hacia arriba a un valor "bonito" para el eje Y (100, 200, 250, 500, 1000...). */
private fun techoEje(maximo: Float): Float {
    if (maximo <= 0f) return 100f
    val magnitud = 10f.pow(floor(log10(maximo)))
    return listOf(1f, 2f, 2.5f, 5f, 10f).map { it * magnitud }.first { it >= maximo }
}

/** Gráfico de barras verticales con líneas guía y etiquetas opcionales en el eje X. */
@Composable
fun GraficoBarras(
    valores: List<Float>,
    colores: List<Color>,
    modifier: Modifier = Modifier,
    etiquetas: List<String>? = null,
    alto: Dp = 150.dp,
    divisiones: Int = 4,
) {
    val medidor = rememberTextMeasurer()
    val estilo = TextStyle(fontSize = 10.sp, color = GrisTexto)
    val maximo = techoEje(valores.maxOrNull() ?: 0f)

    Canvas(modifier.fillMaxWidth().height(alto)) {
        val etiquetasY = (0..divisiones).map { (maximo * it / divisiones).roundToInt().toString() }
        val medidasY = etiquetasY.map { medidor.measure(it, estilo) }
        val separacion = 6.dp.toPx()
        val anchoEje = medidasY.maxOf { it.size.width } + separacion
        val altoEjeX = if (etiquetas != null) medidor.measure("A", estilo).size.height + separacion else 0f
        val margenSuperior = medidasY.first().size.height / 2f
        val areaAlto = size.height - altoEjeX - margenSuperior
        val areaAncho = size.width - anchoEje

        // Líneas guía y etiquetas del eje Y
        for (i in 0..divisiones) {
            val y = margenSuperior + areaAlto * (1f - i / divisiones.toFloat())
            drawLine(LavandaMedia, Offset(anchoEje, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            val medida = medidasY[i]
            drawText(
                medida,
                topLeft = Offset(anchoEje - medida.size.width - separacion, y - medida.size.height / 2f),
            )
        }

        if (valores.isEmpty()) return@Canvas
        val espacio = areaAncho / valores.size
        val anchoBarra = espacio * 0.55f
        valores.forEachIndexed { i, valor ->
            val altoBarra = areaAlto * (valor / maximo).coerceIn(0f, 1f)
            val x = anchoEje + espacio * i + (espacio - anchoBarra) / 2f
            drawRoundRect(
                color = colores[i % colores.size],
                topLeft = Offset(x, margenSuperior + areaAlto - altoBarra),
                size = Size(anchoBarra, altoBarra),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )
            etiquetas?.getOrNull(i)?.let { texto ->
                val medida = medidor.measure(texto, estilo)
                drawText(
                    medida,
                    topLeft = Offset(
                        anchoEje + espacio * i + (espacio - medida.size.width) / 2f,
                        size.height - medida.size.height,
                    ),
                )
            }
        }
    }
}

/** Gráfico de dona a partir de fracciones (0..1) que suman 1. */
@Composable
fun GraficoDona(
    fracciones: List<Float>,
    colores: List<Color>,
    modifier: Modifier = Modifier,
    grosor: Dp = 22.dp,
) {
    Canvas(modifier.aspectRatio(1f)) {
        val trazo = grosor.toPx()
        val diametro = size.minDimension - trazo
        val esquina = Offset((size.width - diametro) / 2f, (size.height - diametro) / 2f)
        val hueco = if (fracciones.size > 1) 3f else 0f
        var inicio = -90f
        if (fracciones.isEmpty()) {
            drawArc(LavandaMedia, 0f, 360f, false, esquina, Size(diametro, diametro), style = Stroke(trazo))
        }
        fracciones.forEachIndexed { i, fraccion ->
            val barrido = 360f * fraccion
            drawArc(
                color = colores[i % colores.size],
                startAngle = inicio + hueco / 2f,
                sweepAngle = (barrido - hueco).coerceAtLeast(0.5f),
                useCenter = false,
                topLeft = esquina,
                size = Size(diametro, diametro),
                style = Stroke(trazo),
            )
            inicio += barrido
        }
    }
}

/** Anillo de progreso con el porcentaje al centro. */
@Composable
fun AnilloPorcentaje(
    fraccion: Float,
    color: Color,
    modifier: Modifier = Modifier,
    tamano: Dp = 76.dp,
    grosor: Dp = 9.dp,
) {
    Box(modifier.size(tamano), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val trazo = grosor.toPx()
            val diametro = size.minDimension - trazo
            val esquina = Offset(trazo / 2f, trazo / 2f)
            drawArc(LavandaMedia, 0f, 360f, false, esquina, Size(diametro, diametro), style = Stroke(trazo))
            drawArc(
                color, -90f, 360f * fraccion.coerceIn(0f, 1f), false, esquina, Size(diametro, diametro),
                style = Stroke(trazo, cap = StrokeCap.Round),
            )
        }
        Text(
            "${(fraccion * 100).roundToInt()}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PurpuraOscuro,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Leyenda(items: List<Pair<String, Color>>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { (texto, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(Modifier.width(6.dp))
                Text(texto, style = MaterialTheme.typography.labelMedium, color = PurpuraOscuro)
            }
        }
    }
}
