package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
fun OrderDetailScreen(idPedido: Int, ordersList: List<Pedido>, onBack: () -> Unit) {
    val pedido = ordersList.find { it.idPedido == idPedido }
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Pedido #$idPedido") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (pedido == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Pedido no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text("Resumen de compra", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fecha:", fontWeight = FontWeight.Medium)
                            Text(pedido.fecha)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Entrega:", fontWeight = FontWeight.Medium)
                            Text("${pedido.tipoEntrega} ${if(pedido.numeroMesa != null) "(Mesa #${pedido.numeroMesa})" else ""}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cliente:", fontWeight = FontWeight.Medium)
                            Text(pedido.nombreCliente)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Pagado", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(format.format(pedido.total), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when(pedido.estado) {
                            "Entregado" -> Color(0xFFE8F5E9)
                            "Pendiente" -> Color(0xFFFFF3E0)
                            "En preparación" -> Color(0xFFE3F2FD)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Estado actual: ${pedido.estado}", fontWeight = FontWeight.Bold)
                            Text(
                                text = when(pedido.estado) {
                                    "Pendiente" -> "Tu pedido está en fila de espera."
                                    "En preparación" -> "Nuestros chefs están cocinando tu orden."
                                    "Entregado" -> "¡Tu pedido ya fue servido! Disfruta."
                                    else -> "Estamos procesando tu solicitud."
                                },
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
