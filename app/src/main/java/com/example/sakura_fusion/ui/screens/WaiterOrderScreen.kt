package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.producto.Producto
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaiterOrderScreen(idMesa: Int, allProducts: List<Producto>, onBack: () -> Unit) {
    var orderItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedido Mesa #$idMesa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (orderItems.isNotEmpty()) {
                Surface(tonalElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total: ${format.format(orderItems.sumOf { it.producto.precio * it.cantidad })}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(onClick = { onBack() }) {
                            Text("Enviar Pedido")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Row(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Lista de productos a la izquierda
            LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                item { Text("Menú", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp)) }
                items(allProducts) { producto ->
                    Card(
                        modifier = Modifier.padding(4.dp).fillMaxWidth(),
                        onClick = {
                            val existing = orderItems.find { it.producto.idProducto == producto.idProducto }
                            if (existing != null) {
                                val index = orderItems.indexOf(existing)
                                val updated = existing.copy(cantidad = existing.cantidad + 1)
                                orderItems = orderItems.toMutableList().apply { set(index, updated) }
                            } else {
                                orderItems = orderItems + CartItem(producto, 1)
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(producto.nombre, fontWeight = FontWeight.Medium)
                            Text(format.format(producto.precio), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Resumen a la derecha
            Column(modifier = Modifier.weight(0.8f).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) {
                Text("Resumen", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                LazyColumn {
                    items(orderItems) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${item.cantidad}x ${item.producto.nombre}", fontSize = 12.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                if (item.cantidad > 1) {
                                    val index = orderItems.indexOf(item)
                                    val updated = item.copy(cantidad = item.cantidad - 1)
                                    orderItems = orderItems.toMutableList().apply { set(index, updated) }
                                } else {
                                    orderItems = orderItems - item
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
