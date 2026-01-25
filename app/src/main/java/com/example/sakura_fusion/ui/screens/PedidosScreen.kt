package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.pedido.Pedido
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    idUsuario: Int, 
    ordersList: List<Pedido>,
    onOrderClick: (Int) -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    val userOrders = ordersList.filter { it.idUsuario == idUsuario }.reversed()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Pedidos", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (userOrders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No tienes pedidos aún.", fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¡Explora nuestro menú!", fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(userOrders) { pedido ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        onClick = { onOrderClick(pedido.idPedido) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Pedido #${pedido.idPedido}", fontWeight = FontWeight.Bold)
                                Text(text = pedido.fecha, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Total: ${format.format(pedido.total)}", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                            Text(
                                text = "Estado: ${pedido.estado}",
                                color = if (pedido.estado == "Entregado") Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
