package com.monexvic.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.monexvic.app.MonexVicApplication
import com.monexvic.app.data.local.MetaAhorro
import com.monexvic.app.data.local.TipoTransaccion
import com.monexvic.app.data.local.Transaccion
import com.monexvic.app.data.repository.FinanzasRepository
import com.monexvic.app.domain.CalculosFinanzas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId

class FinanzasViewModel(private val repository: FinanzasRepository) : ViewModel() {

    private val compartir = SharingStarted.WhileSubscribed(5_000)

    private val transacciones: StateFlow<List<Transaccion>> =
        repository.transacciones.stateIn(viewModelScope, compartir, emptyList())

    // ---------- Inicio ----------
    val inicioState: StateFlow<InicioUiState> =
        combine(transacciones, repository.metas) { lista, metas ->
            val mes = CalculosFinanzas.resumenDelMes(lista, YearMonth.now())
            InicioUiState(
                balance = CalculosFinanzas.disponible(lista, metas),
                enMetas = metas.sumOf { it.montoActual },
                ahorradoMes = mes.ahorro,
                gastadoMes = mes.gastos,
                ultimosMovimientos = lista.take(5),
            )
        }
        .stateIn(viewModelScope, compartir, InicioUiState())

    // ---------- Movimientos ----------
    private val busqueda = MutableStateFlow("")
    private val filtro = MutableStateFlow(FiltroMovimiento.TODOS)

    val movimientosState: StateFlow<MovimientosUiState> =
        combine(transacciones, busqueda, filtro) { lista, texto, filtroActual ->
            val filtradas = lista
                .filter {
                    when (filtroActual) {
                        FiltroMovimiento.TODOS -> true
                        FiltroMovimiento.AHORROS -> it.tipo == TipoTransaccion.AHORRO
                        FiltroMovimiento.GASTOS -> it.tipo == TipoTransaccion.GASTO
                    }
                }
                .filter { texto.isBlank() || it.concepto.contains(texto.trim(), ignoreCase = true) }
            MovimientosUiState(
                busqueda = texto,
                filtro = filtroActual,
                grupos = filtradas
                    .groupBy { CalculosFinanzas.fechaDe(it.fecha) }
                    .map { (fecha, movs) -> GrupoMovimientos(fecha, movs) },
            )
        }.stateIn(viewModelScope, compartir, MovimientosUiState())

    fun onBusquedaChange(texto: String) {
        busqueda.value = texto
    }

    fun onFiltroChange(nuevo: FiltroMovimiento) {
        filtro.value = nuevo
    }

    // ---------- Metas ----------
    val metasState: StateFlow<MetasUiState> =
        combine(transacciones, repository.metas) { lista, metas ->
            MetasUiState(
                metas = metas,
                totalAhorradoEnMetas = metas.sumOf { it.montoActual },
                disponible = CalculosFinanzas.disponible(lista, metas),
            )
        }.stateIn(viewModelScope, compartir, MetasUiState())

    // ---------- Estadísticas ----------
    val estadisticasState: StateFlow<EstadisticasUiState> = transacciones
        .map { lista ->
            val mesActual = YearMonth.now()
            val resumen = CalculosFinanzas.resumenDelMes(lista, mesActual)
            EstadisticasUiState(
                gastosPorCategoria = CalculosFinanzas.gastosPorCategoria(lista, mesActual),
                ultimosMeses = CalculosFinanzas.ultimosMeses(lista, mesActual),
                ingresosMes = resumen.ingresos,
                gastosMes = resumen.gastos,
                porcentajeAhorro = CalculosFinanzas.porcentajeAhorro(
                    resumen.ingresos,
                    resumen.gastos
                ),
            )
        }
        .stateIn(viewModelScope, compartir, EstadisticasUiState())

    // ---------- Acciones ----------
    fun agregarTransaccion(
        concepto: String,
        monto: Double,
        tipo: TipoTransaccion,
        categoria: String,
        fecha: LocalDate = LocalDate.now()
    ) {
        if (concepto.isBlank() || monto <= 0.0) return
        viewModelScope.launch {
            repository.agregarTransaccion(
                Transaccion(
                    concepto = concepto.trim(),
                    monto = monto,
                    tipo = tipo,
                    categoria = categoria,
                    fecha = fecha.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli(),
                )
            )
        }
    }

    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch { repository.eliminarTransaccion(transaccion) }
    }

    fun actualizarTransaccion(
        original: Transaccion,
        concepto: String,
        monto: Double,
        tipo: TipoTransaccion,
        categoria: String,
        fecha: LocalDate,
    ) {
        if (concepto.isBlank() || monto <= 0.0) return
        viewModelScope.launch {
            repository.actualizarTransaccion(
                original.copy(
                    concepto = concepto.trim(),
                    monto = monto,
                    tipo = tipo,
                    categoria = categoria,
                    fecha = if (fecha == CalculosFinanzas.fechaDe(
                            original.fecha
                        )
                    ) {
                        original.fecha
                    } else {
                        fecha.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli()
                    }
                )
            )
        }
    }

    fun restaurarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch { repository.agregarTransaccion(transaccion) }
    }

    fun agregarMeta(nombre: String, objetivo: Double, icono: String) {
        if (nombre.isBlank() || objetivo <= 0.0) return
        viewModelScope.launch {
            repository.agregarMeta(
                MetaAhorro(
                    nombre = nombre.trim(),
                    montoObjetivo = objetivo,
                    icono = icono
                )
            )
        }
    }

    fun aportarAMeta(meta: MetaAhorro, monto: Double) {
        if (monto == 0.0) return
        viewModelScope.launch { repository.aportarAMeta(meta.id, monto) }
    }

    /** Vuelve a insertar una meta eliminada con su mismo id y monto (para "Deshacer"). */
    fun restaurarMeta(meta: MetaAhorro) {
        viewModelScope.launch { repository.agregarMeta(meta) }
    }

    fun eliminarMeta(meta: MetaAhorro) {
        viewModelScope.launch { repository.eliminarMeta(meta) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MonexVicApplication
                FinanzasViewModel(app.repository)
            }
        }
    }
}
