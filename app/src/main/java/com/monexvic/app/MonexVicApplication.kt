package com.monexvic.app

import android.app.Application
import com.monexvic.app.data.local.AppDatabase
import com.monexvic.app.data.repository.FinanzasRepository

/** Contenedor de dependencias manual (sin framework de DI). */
class MonexVicApplication : Application() {

    private val database by lazy { AppDatabase.obtener(this) }

    val repository by lazy {
        FinanzasRepository(database.transaccionDao(), database.metaAhorroDao())
    }
}
