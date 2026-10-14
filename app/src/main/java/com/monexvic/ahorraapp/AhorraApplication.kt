package com.monexvic.ahorraapp

import android.app.Application
import com.monexvic.ahorraapp.data.local.AppDatabase
import com.monexvic.ahorraapp.data.repository.FinanzasRepository

/** Contenedor de dependencias manual (sin framework de DI). */
class AhorraApplication : Application() {

    private val database by lazy { AppDatabase.obtener(this) }

    val repository by lazy {
        FinanzasRepository(database.transaccionDao(), database.metaAhorroDao())
    }
}
