package com.monexvic.ahorraapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metas_ahorro")
data class MetaAhorro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val montoObjetivo: Double,
    val montoActual: Double = 0.0,
    /** Emoji que representa la meta en su tarjeta. */
    val icono: String = "🎯",
) {
    val progreso: Float
        get() = if (montoObjetivo <= 0.0) 0f
        else (montoActual / montoObjetivo).toFloat().coerceIn(0f, 1f)
}
