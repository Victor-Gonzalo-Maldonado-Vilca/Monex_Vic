package com.monexvic.app.domain

import com.monexvic.app.data.local.TipoTransaccion
import com.monexvic.app.data.local.Transaccion
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class CalculosFinanzasTest {

    private val zona = ZoneOffset.UTC
    private val mes = YearMonth.of(2026, 10)

    private fun t(monto: Double, tipo: TipoTransaccion, categoria: String, fecha: LocalDate) =
        Transaccion(
            concepto = categoria, monto = monto, tipo = tipo, categoria = categoria,
            fecha = fecha.atStartOfDay(zona).toInstant().toEpochMilli(),
        )

    private val datos = listOf(
        t(1250.0, TipoTransaccion.AHORRO, "Sueldo", LocalDate.of(2026, 10, 1)),
        t(400.0, TipoTransaccion.GASTO, "Alimentación", LocalDate.of(2026, 10, 3)),
        t(400.0, TipoTransaccion.GASTO, "Servicios", LocalDate.of(2026, 10, 5)),
        t(1000.0, TipoTransaccion.AHORRO, "Sueldo", LocalDate.of(2026, 9, 1)),
        t(600.0, TipoTransaccion.GASTO, "Ocio", LocalDate.of(2026, 9, 10)),
    )

    @Test
    fun balanceRestaGastosDeIngresos() {
        assertEquals(850.0, CalculosFinanzas.balance(datos), 0.001)
    }

    @Test
    fun resumenDelMesSoloCuentaEseMes() {
        val r = CalculosFinanzas.resumenDelMes(datos, mes, zona)
        assertEquals(1250.0, r.ingresos, 0.001)
        assertEquals(800.0, r.gastos, 0.001)
        assertEquals(450.0, r.ahorro, 0.001)
    }

    @Test
    fun gastosPorCategoriaCalculaPorcentajes() {
        val r = CalculosFinanzas.gastosPorCategoria(datos, mes, zona)
        assertEquals(2, r.size)
        assertEquals(0.5f, r[0].porcentaje, 0.001f)
        assertEquals(0.5f, r[1].porcentaje, 0.001f)
    }

    @Test
    fun ultimosMesesDevuelveMesesEnOrden() {
        val r = CalculosFinanzas.ultimosMeses(datos, mes, cantidad = 2, zona = zona)
        assertEquals(listOf(YearMonth.of(2026, 9), mes), r.map { it.mes })
        assertEquals(400.0, r[0].ahorro, 0.001)
    }

    @Test
    fun porcentajeAhorroSeLimitaEntreCeroYUno() {
        assertEquals(0.36f, CalculosFinanzas.porcentajeAhorro(1250.0, 800.0), 0.001f)
        assertEquals(0f, CalculosFinanzas.porcentajeAhorro(100.0, 300.0), 0.001f)
        assertEquals(0f, CalculosFinanzas.porcentajeAhorro(0.0, 50.0), 0.001f)
    }
}
