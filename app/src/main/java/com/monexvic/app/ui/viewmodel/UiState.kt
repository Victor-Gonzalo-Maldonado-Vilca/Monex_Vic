package com.monexvic.app.ui.viewmodel

import com.monexvic.app.data.local.MetaAhorro
import com.monexvic.app.data.local.Transaccion
import com.monexvic.app.domain.CategoriaResumen
import com.monexvic.app.domain.MesResumen
import java.time.LocalDate

enum class FiltroMovimiento(val etiqueta: String) {
    TODOS("Todos"),
    AHORROS("Ahorros"),
    GASTOS("Gastos"),
}

data class InicioUiState(
    val balance: Double = 0.0,
    val enMetas: Double = 0.0,
    val ahorradoMes: Double = 0.0,
    val gastadoMes: Double = 0.0,
    val ultimosMovimientos: List<Transaccion> = emptyList(),
)

data class GrupoMovimientos(
    val fecha: LocalDate,
    val movimientos: List<Transaccion>,
)

data class MovimientosUiState(
    val busqueda: String = "",
    val filtro: FiltroMovimiento = FiltroMovimiento.TODOS,
    val grupos: List<GrupoMovimientos> = emptyList(),
)

data class MetasUiState(
    val metas: List<MetaAhorro> = emptyList(),
    val totalAhorradoEnMetas: Double = 0.0,
    val disponible: Double = 0.0,
)

data class EstadisticasUiState(
    val gastosPorCategoria: List<CategoriaResumen> = emptyList(),
    val ultimosMeses: List<MesResumen> = emptyList(),
    val ingresosMes: Double = 0.0,
    val gastosMes: Double = 0.0,
    val porcentajeAhorro: Float = 0f,
) {
    val mayorCategoria: CategoriaResumen? get() = gastosPorCategoria.firstOrNull()
}
