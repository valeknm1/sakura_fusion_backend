package com.example.sakura_fusion.data

import com.example.sakura_fusion.data.producto.Producto

data class CartItem(
    val producto: Producto,
    var cantidad: Int
)
