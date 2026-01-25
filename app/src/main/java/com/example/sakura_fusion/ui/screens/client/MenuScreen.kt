package com.example.sakura_fusion.ui.screens.client

import androidx.compose.runtime.Composable
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.ui.screens.MenuScreen as BaseMenuScreen

@Composable
fun MenuScreen(onOrderConfirmed: (Pedido) -> Unit) {
    BaseMenuScreen(onOrderConfirmed = onOrderConfirmed)
}
