package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.pedido.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChefOrdersScreen(pedidosPendientes: List<Pedido>, onMarkReady: (Pedido) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Pedidos en Cocina") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Órdenes a Preparar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (pedidosPendientes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay pedidos pendientes.")
                }
            } else {
                LazyColumn {
                    items(pedidosPendientes) { pedido ->
                        ChefOrderItem(pedido = pedido, onMarkReady = { onMarkReady(pedido) })
                    }
                }
            }
        }
    }
}

@Composable
fun ChefOrderItem(pedido: Pedido, onMarkReady: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Pedido #${pedido.idPedido}", fontWeight = FontWeight.Bold)
                Text("Estado: ${pedido.estado}")
                Text("Cliente: ${pedido.nombreCliente}", fontSize = 12.sp)
            }
            Button(onClick = onMarkReady) {
                Icon(Icons.Default.Check, contentDescription = null)
                Text("Listo")
            }
        }
    }
}
