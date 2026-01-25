package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.mesa.Mesa
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.reserva.Reserva
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaiterDashboardScreen(
    allOrders: List<Pedido>, 
    allReservations: List<Reserva>,
    onDeliverOrder: (Pedido) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Mesas") }, icon = { Icon(Icons.Default.List, null) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Pedidos") }, icon = { Icon(Icons.Default.ShoppingCart, null) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Reservas") }, icon = { Icon(Icons.Default.DateRange, null) })
        }
        
        when (selectedTab) {
            0 -> TablesTab(allOrders)
            1 -> WaiterOrdersTab(allOrders, onDeliverOrder)
            2 -> WaiterReservationsTab(allReservations)
        }
    }
}

@Composable
fun TablesTab(allOrders: List<Pedido>) {
    val mesas = remember {
        listOf(
            Mesa(1, 1, 2, true),
            Mesa(2, 2, 4, true),
            Mesa(3, 3, 2, false),
            Mesa(4, 4, 6, true),
            Mesa(5, 5, 4, true),
            Mesa(6, 6, 2, true),
            Mesa(7, 7, 8, true),
            Mesa(8, 8, 4, true)
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestión de Salón", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Vista en tiempo real de disponibilidad.", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mesas) { mesa ->
                WaiterTableCard(mesa, allOrders)
            }
        }
    }
}

@Composable
fun WaiterOrdersTab(allOrders: List<Pedido>, onDeliver: (Pedido) -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cola de Pedidos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (allOrders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay pedidos registrados.")
            }
        } else {
            LazyColumn {
                items(allOrders.reversed()) { pedido ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when(pedido.estado) {
                                "Listo" -> Color(0xFFE8F5E9)
                                "Pendiente" -> Color(0xFFFFF3E0)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pedido #${pedido.idPedido}", fontWeight = FontWeight.Bold)
                                Text(pedido.fecha, fontSize = 12.sp)
                            }
                            Text("Monto: ${format.format(pedido.total)}")
                            Text("Entrega: ${pedido.tipoEntrega} ${if(pedido.numeroMesa != null) "(Mesa #${pedido.numeroMesa})" else ""}")
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Estado: ${pedido.estado}", 
                                    fontWeight = FontWeight.Bold,
                                    color = when(pedido.estado) {
                                        "Listo" -> Color(0xFF2E7D32)
                                        "Pendiente" -> Color(0xFFE65100)
                                        else -> Color.Unspecified
                                    }
                                )
                                
                                if (pedido.estado == "Listo") {
                                    Button(
                                        onClick = { onDeliver(pedido) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Llevar a la Mesa", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaiterReservationsTab(allReservations: List<Reserva>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Agenda de Reservas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Horarios programados para hoy.", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        if (allReservations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay reservas programadas.")
            }
        } else {
            LazyColumn {
                items(allReservations.sortedByDescending { it.idReserva }) { reserva ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cliente: ${reserva.nombreCliente}", fontWeight = FontWeight.Bold)
                                Text("Fecha: ${reserva.fecha}", fontSize = 14.sp)
                                Text("Hora: ${reserva.hora}", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                                Text("Personas: ${reserva.cantPersonas} - Mesa #${reserva.idMesa}")
                            }
                            
                            Text(
                                reserva.estado, 
                                color = if (reserva.estado == "Confirmada") Color(0xFF388E3C) else Color(0xFFE65100), 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaiterTableCard(mesa: Mesa, allOrders: List<Pedido>) {
    var showOrderDialog by remember { mutableStateOf(false) }
    val orderForTable = allOrders.find { it.numeroMesa == mesa.numero && it.estado != "Entregado" }
    val isOcupada = !mesa.disponible || orderForTable != null

    Card(
        modifier = Modifier.fillMaxWidth().height(130.dp),
        colors = CardDefaults.cardColors(containerColor = if (!isOcupada) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Mesa ${mesa.numero}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = if (!isOcupada) "DISPONIBLE" else "OCUPADA", color = if (!isOcupada) Color(0xFF2E7D32) else Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if (isOcupada) {
                Button(onClick = { showOrderDialog = true }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
                    Text("Ver Pedido", fontSize = 11.sp)
                }
            } else {
                Text("Cap: ${mesa.capacidad}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }

    if (showOrderDialog) {
        AlertDialog(
            onDismissRequest = { showOrderDialog = false },
            title = { Text("Pedido Mesa #${mesa.numero}") },
            text = {
                if (orderForTable != null) {
                    Column {
                        Text("ID: #${orderForTable.idPedido}", fontWeight = FontWeight.Bold)
                        Text("Total: ${NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(orderForTable.total)}")
                        Text("Estado: ${orderForTable.estado}")
                    }
                } else {
                    Text("Mesa ocupada manualmente.")
                }
            },
            confirmButton = { Button(onClick = { showOrderDialog = false }) { Text("Cerrar") } }
        )
    }
}
