package com.monexvic.app.data.repository

import com.monexvic.app.data.local.MetaAhorro
import com.monexvic.app.data.local.MetaAhorroDao
import com.monexvic.app.data.local.Transaccion
import com.monexvic.app.data.local.TransaccionDao
import kotlinx.coroutines.flow.Flow

/** Única fuente de verdad para los datos financieros (Room). */
class FinanzasRepository(
    private val transaccionDao: TransaccionDao,
    private val metaAhorroDao: MetaAhorroDao,
) {
    val transacciones: Flow<List<Transaccion>> = transaccionDao.observarTodas()
    val metas: Flow<List<MetaAhorro>> = metaAhorroDao.observarTodas()

    suspend fun agregarTransaccion(transaccion: Transaccion) = transaccionDao.insertar(transaccion)
    suspend fun eliminarTransaccion(transaccion: Transaccion) = transaccionDao.eliminar(transaccion)

    suspend fun actualizarTransaccion(transaccion: Transaccion) = transaccionDao.actualizar(transaccion)

    suspend fun agregarMeta(meta: MetaAhorro) = metaAhorroDao.insertar(meta)
    suspend fun aportarAMeta(id: Long, monto: Double) = metaAhorroDao.aportar(id, monto)
    suspend fun eliminarMeta(meta: MetaAhorro) = metaAhorroDao.eliminar(meta)
}
