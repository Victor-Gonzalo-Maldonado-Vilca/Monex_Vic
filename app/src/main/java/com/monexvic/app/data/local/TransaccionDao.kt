package com.monexvic.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC, id DESC")
    fun observarTodas(): Flow<List<Transaccion>>

    @Insert
    suspend fun insertar(transaccion: Transaccion): Long

    @Delete
    suspend fun eliminar(transaccion: Transaccion)

    @Update
    suspend fun actualizar(transaccion: Transaccion)
}
