package com.monexvic.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.monexvic.app.data.local.TipoTransaccion
import com.monexvic.app.ui.theme.LavandaMedia
import com.monexvic.app.ui.theme.MentaClara
import com.monexvic.app.ui.theme.MentaOscuro
import com.monexvic.app.ui.theme.Purpura
import com.monexvic.app.ui.theme.RosaClaro

data class CategoriaUi(
    val icono: ImageVector,
    val fondo: Color,
    val tinte: Color,
)

object Categorias {
    val gastos = listOf("Alimentación", "Servicios", "Transporte", "Ocio", "Salud", "Educación", "Otros")
    val ingresos = listOf("Sueldo", "Ahorro", "Extra")

    fun de(tipo: TipoTransaccion): List<String> =
        if (tipo == TipoTransaccion.AHORRO) ingresos else gastos

    private val rosa = Color(0xFFC2307A)

    fun ui(categoria: String): CategoriaUi = when (categoria) {
        "Sueldo" -> CategoriaUi(Icons.Filled.Payments, MentaClara, MentaOscuro)
        "Ahorro" -> CategoriaUi(Icons.Filled.Savings, MentaClara, MentaOscuro)
        "Extra" -> CategoriaUi(Icons.Filled.AttachMoney, MentaClara, MentaOscuro)
        "Alimentación" -> CategoriaUi(Icons.Filled.ShoppingCart, RosaClaro, rosa)
        "Servicios" -> CategoriaUi(Icons.Filled.Handyman, LavandaMedia, Purpura)
        "Transporte" -> CategoriaUi(Icons.Filled.DirectionsCar, RosaClaro, rosa)
        "Ocio" -> CategoriaUi(Icons.Filled.Celebration, LavandaMedia, Purpura)
        "Salud" -> CategoriaUi(Icons.Filled.LocalHospital, RosaClaro, rosa)
        "Educación" -> CategoriaUi(Icons.Filled.School, LavandaMedia, Purpura)
        else -> CategoriaUi(Icons.Filled.Category, LavandaMedia, Purpura)
    }
}
