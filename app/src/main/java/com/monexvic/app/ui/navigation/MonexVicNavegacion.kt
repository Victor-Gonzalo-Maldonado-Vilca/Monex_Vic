package com.monexvic.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.monexvic.app.ui.screens.EstadisticasScreen
import com.monexvic.app.ui.screens.InicioScreen
import com.monexvic.app.ui.screens.MetasScreen
import com.monexvic.app.ui.screens.MovimientosScreen
import com.monexvic.app.ui.theme.Blanco
import com.monexvic.app.ui.theme.FondoPantalla
import com.monexvic.app.ui.theme.GrisTexto
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.viewmodel.FinanzasViewModel

enum class Destino(val ruta: String, val etiqueta: String, val icono: ImageVector) {
    INICIO("inicio", "Inicio", Icons.Filled.Home),
    MOVIMIENTOS("movimientos", "Movimientos", Icons.Filled.SwapHoriz),
    METAS("metas", "Metas", Icons.Filled.Public),
    ESTADISTICAS("estadisticas", "Estadísticas", Icons.Filled.PieChart),
}

/** Raíz de la UI: fondo degradado, barra de navegación inferior y NavHost con las 4 pestañas. */
@Composable
fun MonexVicRaiz(viewModel: FinanzasViewModel = viewModel(factory = FinanzasViewModel.Factory)) {
    val navController = rememberNavController()

    Box(
        Modifier
            .fillMaxSize()
            .background(FondoPantalla)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { BarraNavegacion(navController) },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Destino.INICIO.ruta,
                modifier = Modifier.padding(padding),
            ) {
                composable(Destino.INICIO.ruta) {
                    InicioScreen(
                        viewModel = viewModel,
                        onVerMovimientos = { navController.irA(Destino.MOVIMIENTOS) },
                    )
                }
                composable(Destino.MOVIMIENTOS.ruta) { MovimientosScreen(viewModel) }
                composable(Destino.METAS.ruta) { MetasScreen(viewModel) }
                composable(Destino.ESTADISTICAS.ruta) { EstadisticasScreen(viewModel) }
            }
        }
    }
}

@Composable
private fun BarraNavegacion(navController: NavHostController) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoActual = entradaActual?.destination

    NavigationBar(containerColor = Blanco) {
        Destino.entries.forEach { destino ->
            val seleccionado = destinoActual?.hierarchy?.any { it.route == destino.ruta } == true
            NavigationBarItem(
                selected = seleccionado,
                onClick = { navController.irA(destino) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = {
                    Text(
                        destino.etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Purpura,
                    selectedTextColor = Purpura,
                    indicatorColor = LavandaMedia,
                    unselectedIconColor = GrisTexto,
                    unselectedTextColor = GrisTexto,
                ),
            )
        }
    }
}

private fun NavHostController.irA(destino: Destino) {
    navigate(destino.ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
