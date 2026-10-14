package com.monexvic.ahorraapp.ui.components

import com.monexvic.ahorraapp.data.local.TipoTransaccion
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val localePe: Locale = Locale.forLanguageTag("es-PE")
private val simbolos = DecimalFormatSymbols(Locale.US)
private val formatoDecimal = DecimalFormat("#,##0.00", simbolos)
private val formatoCorto = DecimalFormat("#,##0.##", simbolos)
private val formatoFecha = DateTimeFormatter.ofPattern("d 'de' MMM", localePe)

/** S/ 1,250.00 */
fun formatoSoles(monto: Double): String = "S/ " + formatoDecimal.format(monto)

/** S/ 650 (sin decimales si no los tiene) */
fun formatoSolesCorto(monto: Double): String = "S/ " + formatoCorto.format(monto)

/** + S/ 1,250.00  ó  - S/ 800.00 */
fun formatoConSigno(monto: Double, tipo: TipoTransaccion): String =
    (if (tipo == TipoTransaccion.AHORRO) "+ " else "- ") + formatoSoles(monto)

fun etiquetaFecha(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): String = when (fecha) {
    hoy -> "Hoy"
    hoy.minusDays(1) -> "Ayer"
    else -> fecha.format(formatoFecha).replace(".", "")
}

fun etiquetaMes(mes: YearMonth): String =
    mes.month.getDisplayName(TextStyle.SHORT, localePe)
        .replace(".", "")
        .replaceFirstChar { it.titlecase(localePe) }

fun nombreMes(mes: YearMonth): String =
    mes.month.getDisplayName(TextStyle.FULL, localePe)
        .replaceFirstChar { it.titlecase(localePe) }
