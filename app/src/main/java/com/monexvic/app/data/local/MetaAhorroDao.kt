package com.monexvic.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaAhorroDao {

    @Query("SELECT * FROM metas_ahorro ORDER BY id ASC")
    fun observarTodas(): Flow<List<MetaAhorro>>

    @Insert
    suspend fun insertar(meta: MetaAhorro): Long

    @Query("UPDATE metas_ahorro SET montoActual = MAX(0, montoActual + :monto) WHERE id = :id")
    suspend fun aportar(id: Long, monto: Double)

    @Delete
    suspend fun eliminar(meta: MetaAhorro)
}
