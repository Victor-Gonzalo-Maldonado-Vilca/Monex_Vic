package com.monexvic.ahorraapp.domain

import com.monexvic.ahorraapp.data.local.TipoTransaccion
import com.monexvic.ahorraapp.data.local.Transaccion
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class CategoriaResumen(
    val categoria: String,
    val total: Double,
    /** Fracción del total de gastos (0..1). */
    val porcentaje: Float,
)

data class MesResumen(
    val mes: YearMonth,
    val ingresos: Double,
    val gastos: Double,
) {
    val ahorro: Double get() = ingresos - gastos
}

/** Funciones puras de cálculo; no dependen de Android para poder probarse fácilmente. */
object CalculosFinanzas {

    fun fechaDe(millis: Long, zona: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(millis).atZone(zona).toLocalDate()

    fun mesDe(millis: Long, zona: ZoneId = ZoneId.systemDefault()): YearMonth =
        YearMonth.from(fechaDe(millis, zona))

    /** Balance disponible = todo lo que entró menos todo lo que salió. */
    fun balance(transacciones: List<Transaccion>): Double =
        transacciones.sumOf { if (it.tipo == TipoTransaccion.AHORRO) it.monto else -it.monto }

    fun totalDelMes(
        transacciones: List<Transaccion>,
        tipo: TipoTransaccion,
        mes: YearMonth,
        zona: ZoneId = ZoneId.systemDefault(),
    ): Double = transacciones
        .filter { it.tipo == tipo && mesDe(it.fecha, zona) == mes }
        .sumOf { it.monto }

    fun resumenDelMes(
        transacciones: List<Transaccion>,
        mes: YearMonth,
        zona: ZoneId = ZoneId.systemDefault(),
    ) = MesResumen(
        mes = mes,
        ingresos = totalDelMes(transacciones, TipoTransaccion.AHORRO, mes, zona),
        gastos = totalDelMes(transacciones, TipoTransaccion.GASTO, mes, zona),
    )

    /** Gastos del mes agrupados por categoría, de mayor a menor. */
    fun gastosPorCategoria(
        transacciones: List<Transaccion>,
        mes: YearMonth,
        zona: ZoneId = ZoneId.systemDefault(),
    ): List<CategoriaResumen> {
        val gastos = transacciones.filter {
            it.tipo == TipoTransaccion.GASTO && mesDe(it.fecha, zona) == mes
        }
        val total = gastos.sumOf { it.monto }
        if (total <= 0.0) return emptyList()
        return gastos
            .groupBy { it.categoria }
            .map { (categoria, lista) ->
                val suma = lista.sumOf { it.monto }
                CategoriaResumen(categoria, suma, (suma / total).toFloat())
            }
            .sortedByDescending { it.total }
    }

    /** Resumen de los últimos [cantidad] meses terminando en [hasta] (inclusive). */
    fun ultimosMeses(
        transacciones: List<Transaccion>,
        hasta: YearMonth,
        cantidad: Int = 6,
        zona: ZoneId = ZoneId.systemDefault(),
    ): List<MesResumen> = (cantidad - 1 downTo 0).map { atras ->
        resumenDelMes(transacciones, hasta.minusMonths(atras.toLong()), zona)
    }

    /** Porcentaje de los ingresos que se logró ahorrar (0..1). */
    fun porcentajeAhorro(ingresos: Double, gastos: Double): Float =
        if (ingresos <= 0.0) 0f
        else ((ingresos - gastos) / ingresos).toFloat().coerceIn(0f, 1f)
}
