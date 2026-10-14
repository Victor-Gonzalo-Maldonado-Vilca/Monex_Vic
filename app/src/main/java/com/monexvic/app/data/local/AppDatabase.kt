package com.monexvic.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDate
import java.time.ZoneId

@Database(
    entities = [Transaccion::class, MetaAhorro::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transaccionDao(): TransaccionDao
    abstract fun metaAhorroDao(): MetaAhorroDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ahorra_app.db",
                )
                    .addCallback(DatosIniciales)
                    .build()
                    .also { instancia = it }
            }
    }

    /** Carga datos de ejemplo la primera vez que se crea la base de datos. */
    private object DatosIniciales : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            val hoy = LocalDate.now()

            fun insertarTransaccion(
                concepto: String, monto: Double, tipo: TipoTransaccion,
                categoria: String, fecha: LocalDate,
            ) {
                val millis = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                db.execSQL(
                    "INSERT INTO transacciones (concepto, monto, tipo, categoria, fecha) VALUES (?, ?, ?, ?, ?)",
                    arrayOf(concepto, monto, tipo.name, categoria, millis),
                )
            }

            // Cinco meses anteriores con distintos niveles de gasto
            val gastosPrevios = listOf(700.0, 640.0, 430.0, 780.0, 650.0)
            gastosPrevios.forEachIndexed { i, gastoTotal ->
                val mes = hoy.minusMonths((gastosPrevios.size - i).toLong()).withDayOfMonth(1)
                insertarTransaccion("Sueldo", 1250.0, TipoTransaccion.AHORRO, "Sueldo", mes)
                insertarTransaccion("Supermercado", gastoTotal * 0.5, TipoTransaccion.GASTO, "Alimentación", mes.plusDays(5))
                insertarTransaccion("Luz y agua", gastoTotal * 0.3, TipoTransaccion.GASTO, "Servicios", mes.plusDays(12))
                insertarTransaccion("Cine", gastoTotal * 0.2, TipoTransaccion.GASTO, "Ocio", mes.plusDays(20))
            }

            // Mes actual: S/ 1,250 de ingreso y S/ 800 de gasto
            fun dia(d: Int) = hoy.withDayOfMonth(minOf(d, hoy.dayOfMonth))
            insertarTransaccion("Sueldo", 1250.0, TipoTransaccion.AHORRO, "Sueldo", dia(1))
            insertarTransaccion("Supermercado", 400.0, TipoTransaccion.GASTO, "Alimentación", dia(3))
            insertarTransaccion("Servicios", 150.0, TipoTransaccion.GASTO, "Servicios", dia(5))
            insertarTransaccion("Taxi", 50.0, TipoTransaccion.GASTO, "Transporte", dia(7))
            insertarTransaccion("Concierto", 200.0, TipoTransaccion.GASTO, "Ocio", dia(9))

            fun insertarMeta(nombre: String, objetivo: Double, actual: Double, icono: String) {
                db.execSQL(
                    "INSERT INTO metas_ahorro (nombre, montoObjetivo, montoActual, icono) VALUES (?, ?, ?, ?)",
                    arrayOf(nombre, objetivo, actual, icono),
                )
            }
            insertarMeta("Viaje a la playa", 1000.0, 650.0, "🏝️")
            insertarMeta("Fondo de Emergencia", 1000.0, 650.0, "🛡️")
            insertarMeta("Laptop nueva", 2500.0, 300.0, "💻")
        }
    }
}
