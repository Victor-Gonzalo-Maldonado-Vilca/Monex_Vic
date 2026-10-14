package com.monexvic.ahorraapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** AHORRO = dinero que entra (sueldo, ingresos, ahorros). GASTO = dinero que sale. */
enum class TipoTransaccion { AHORRO, GASTO }

@Entity(
    tableName = "transacciones",
    indices = [Index("fecha")],
)
data class Transaccion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val concepto: String,
    val monto: Double,
    val tipo: TipoTransaccion,
    val categoria: String,
    /** Fecha en milisegundos (epoch). */
    val fecha: Long,
)
