package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.producto.Producto
import com.example.sakura_fusion.data.reserva.Reserva
import com.example.sakura_fusion.validations.ProductValidations
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    allOrders: List<Pedido>, 
    allReservations: List<Reserva>,
    onConfirmReserva: (Reserva) -> Unit = {},
    onClearOrders: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Lista de productos mock para el admin
    var inventoryList by remember { mutableStateOf<List<Producto>>(listOf(
        Producto(1, "Sushi Sakura", "Salmón y aguacate", 8990.0, 10, 2, "https://plus.unsplash.com/premium_photo-1668143358351-b20146dbcc02?q=80&w=500&auto=format&fit=crop", "Sushi"),
        Producto(2, "Ramen Tonkotsu", "Cerdo y fideos", 11500.0, 5, 3, "https://images.unsplash.com/photo-1557872245-741f4c666e5c?q=80&w=500&auto=format&fit=crop", "Ramen"),
        Producto(3, "Gyoza", "Empanadillas japonesas", 5500.0, 20, 1, "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?q=80&w=500&auto=format&fit=crop", "Entradas"),
        Producto(4, "Mochi Fresa", "Postre de arroz", 3200.0, 15, 5, "https://images.unsplash.com/photo-1563805042-7684c019e1cb?q=80&w=500&auto=format&fit=crop", "Postres")
    )) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Menú") }, icon = { Icon(Icons.AutoMirrored.Filled.List, null) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Ventas") }, icon = { Icon(Icons.Default.ShoppingCart, null) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Reservas") }, icon = { Icon(Icons.Default.DateRange, null) })
        }
        
        when (selectedTab) {
            0 -> InventoryTab(
                inventory = inventoryList, 
                onUpdate = { inventoryList = it }
            )
            1 -> OrdersTab(allOrders, onClearOrders)
            2 -> AdminReservationsTab(allReservations, onConfirmReserva)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTab(
    inventory: List<Producto>, 
    onUpdate: (List<Producto>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Producto?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                editingProduct = null
                showDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Gestión de Inventario (${inventory.size} platos)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn {
                items(inventory) { producto ->
                    AdminProductItem(
                        producto = producto,
                        onEdit = {
                            editingProduct = it
                            showDialog = true
                        },
                        onDelete = {
                            onUpdate(inventory.filter { it.idProducto != producto.idProducto })
                        }
                    )
                }
            }
        }

        if (showDialog) {
            ProductFormDialog(
                product = editingProduct,
                onDismiss = { showDialog = false },
                onSave = { newProduct ->
                    if (editingProduct == null) {
                        onUpdate(inventory + newProduct.copy(idProducto = (inventory.maxOfOrNull { it.idProducto } ?: 0) + 1))
                    } else {
                        onUpdate(inventory.map { if (it.idProducto == newProduct.idProducto) newProduct else it })
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun ProductFormDialog(
    product: Producto?,
    onDismiss: () -> Unit,
    onSave: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(product?.nombre ?: "") }
    var desc by remember { mutableStateOf(product?.descripcion ?: "") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }

    val isNombreValid = ProductValidations.isValidName(nombre)
    val isDescValid = ProductValidations.isValidDescription(desc)
    val isPrecioValid = precio.toDoubleOrNull()?.let { ProductValidations.isValidPrice(it) } ?: false
    val isStockValid = stock.toIntOrNull()?.let { ProductValidations.isValidStock(it) } ?: false

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Nuevo Plato" else "Editar Plato") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (mín. 3)") },
                    isError = nombre.isNotEmpty() && !isNombreValid,
                    supportingText = { if (nombre.isNotEmpty() && !isNombreValid) Text("Muy corto") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción (mín. 10)") },
                    isError = desc.isNotEmpty() && !isDescValid,
                    supportingText = { if (desc.isNotEmpty() && !isDescValid) Text("Muy corta") }
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precio = it },
                    label = { Text("Precio (CLP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = precio.isNotEmpty() && !isPrecioValid,
                    supportingText = { if (precio.isNotEmpty() && !isPrecioValid) Text("Precio inválido") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { if (it.all { c -> c.isDigit() }) stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = stock.isNotEmpty() && !isStockValid,
                    supportingText = { if (stock.isNotEmpty() && !isStockValid) Text("Stock inválido") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(Producto(
                        idProducto = product?.idProducto ?: 0,
                        nombre = nombre,
                        descripcion = desc,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        idCategoria = product?.idCategoria ?: 1,
                        imagenUrl = product?.imagenUrl,
                        nombreCategoria = product?.nombreCategoria ?: "General"
                    ))
                },
                enabled = isNombreValid && isDescValid && isPrecioValid && isStockValid
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun OrdersTab(allOrders: List<Pedido>, onClear: () -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Historial de Ventas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Refresh, contentDescription = "Limpiar Pedidos", tint = MaterialTheme.colorScheme.error)
            }
        }
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pedido #${pedido.idPedido}", fontWeight = FontWeight.Bold)
                                Text(pedido.fecha, fontSize = 12.sp)
                            }
                            Text("Cliente: ${pedido.nombreCliente}", fontWeight = FontWeight.Medium)
                            Text("Total: ${format.format(pedido.total)}", color = MaterialTheme.colorScheme.primary)
                            Text("Estado: ${pedido.estado}", fontWeight = FontWeight.Medium, color = getStatusColor(pedido.estado))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReservationsTab(
    allReservations: List<Reserva>,
    onConfirm: (Reserva) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Control de Reservas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (allReservations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay reservas registradas.")
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
                                Text("Hora: ${reserva.hora}", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                                Text("Personas: ${reserva.cantPersonas} - Mesa #${reserva.idMesa}")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (reserva.estado == "Pendiente") {
                                    Button(
                                        onClick = { onConfirm(reserva) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                                    ) {
                                        Text("Confirmar", fontSize = 12.sp)
                                    }
                                } else {
                                    Text(
                                        reserva.estado, 
                                        color = Color(0xFF388E3C), 
                                        fontWeight = FontWeight.Bold
                                    )
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
fun getStatusColor(status: String): Color {
    return when (status) {
        "Pendiente" -> Color(0xFFE65100)
        "En preparación" -> Color(0xFF1976D2)
        "Entregado" -> Color(0xFF388E3C)
        "Listo" -> Color(0xFF4CAF50)
        "Cancelado" -> Color.Red
        else -> Color.Black
    }
}

@Composable
fun AdminProductItem(
    producto: Producto, 
    onEdit: (Producto) -> Unit,
    onDelete: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("Categoría: ${producto.nombreCategoria}", fontSize = 12.sp, color = Color.Gray)
                Text("Precio: ${format.format(producto.precio)}")
                Text("Stock: ${producto.stock}", color = if (producto.stock < 5) Color.Red else Color.Black)
            }
            Row {
                IconButton(onClick = { onEdit(producto) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}
