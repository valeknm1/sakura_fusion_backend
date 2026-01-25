package com.example.sakura_fusion.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sakura_fusion.R
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.producto.Producto
import com.example.sakura_fusion.ui.viewmodel.MenuViewModel
import java.text.NumberFormat
import java.util.*

// IE 2.3.1: Modelo auxiliar para el carrito (No es entidad de BD)
data class CartItem(
    val producto: Producto,
    var cantidad: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onOrderConfirmed: (Pedido) -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val filteredProductos by viewModel.filteredProductos.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    val categories = listOf("Todos", "Entradas", "Sushi", "Ramen", "Bebidas", "Postres")
    var showCartDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Sakura Fusion", 
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                actions = {
                    BadgedBox(
                        badge = { 
                            if(cart.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) { 
                                    Text(cart.sumOf { it.cantidad }.toString()) 
                                } 
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        IconButton(
                            onClick = { showCartDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(padding)) {
                // Buscador con animación
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    placeholder = { Text("¿Qué te apetece hoy?") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { 
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )

                // Filtros de Categoría con Scroll suave
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategoryChange(category) },
                            label = { Text(category) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // IE 2.2.2: Animación de la lista al filtrar
                AnimatedContent(
                    targetState = filteredProductos,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "ListAnimation"
                ) { targetList ->
                    if (targetList.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No encontramos lo que buscas", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(targetList, key = { it.idProducto }) { producto ->
                                ProductoItem(producto) {
                                    viewModel.addToCart(producto)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCartDialog) {
            CartDialog(
                cart = cart, 
                onDismiss = { showCartDialog = false }, 
                onAdd = { viewModel.addToCart(it) },
                onRemove = { viewModel.removeFromCart(it) },
                onConfirm = { tipo, mesa ->
                    val nuevoPedido = viewModel.createPedido(tipo, mesa)
                    onOrderConfirmed(nuevoPedido)
                    showCartDialog = false
                    showSuccessDialog = true
                }
            )
        }

        // IE 2.2.2: Animación del diálogo de éxito
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                icon = { 
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = Color(0xFF4CAF50), 
                        modifier = Modifier.size(64.dp)
                    ) 
                },
                title = { Text("¡Éxito!", fontWeight = FontWeight.Bold) },
                text = { Text("Tu pedido ha sido enviado a la cocina. ¡Prepárate para disfrutar!") },
                confirmButton = {
                    Button(
                        onClick = { showSuccessDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Genial")
                    }
                }
            )
        }
    }
}

@Composable
fun ProductoItem(producto: Producto, onAddToCart: () -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val haptic = LocalHapticFeedback.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IE 2.1.2: Lógica para cargar imagen local o remota
            val imageSource: Any? = if (producto.imagenUrl == "mochi_fresa") {
                R.drawable.mochi_fresa
            } else {
                producto.imagenUrl
            }

            AsyncImage(
                model = imageSource,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    producto.nombre, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    producto.descripcion, 
                    fontSize = 13.sp, 
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    format.format(producto.precio), 
                    fontWeight = FontWeight.ExtraBold, 
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
            }
            
            // Botón añadir con feedback háptico (IE 2.4.1)
            FilledIconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddToCart()
                },
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun CartDialog(
    cart: List<CartItem>, 
    onDismiss: () -> Unit, 
    onAdd: (Producto) -> Unit,
    onRemove: (Producto) -> Unit,
    onConfirm: (String, Int?) -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    var deliveryType by remember { mutableStateOf("Para llevar") }
    var tableNumber by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tu Carrito", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(cart) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), 
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.producto.nombre, modifier = Modifier.weight(1f))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onRemove(item.producto) }) {
                                    // IE 2.1.2: Iconos adecuados (Menos/Borrar)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (item.cantidad > 1) MaterialTheme.colorScheme.primary else Color.Red
                                    )
                                }
                                Text(
                                    text = "${item.cantidad}",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(onClick = { onAdd(item.producto) }) {
                                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            
                            Text(
                                format.format(item.producto.precio * item.cantidad), 
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total a pagar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(format.format(cart.sumOf { it.producto.precio * it.cantidad }), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("¿Dónde comerás?", fontWeight = FontWeight.Bold)
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf("Para llevar", "En mesa").forEach { text ->
                        val isSelected = (text == deliveryType)
                        OutlinedButton(
                            onClick = { deliveryType = text },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = !isSelected)
                        ) {
                            Text(text)
                        }
                    }
                }
                
                // IE 2.2.2: Animación al mostrar el campo de mesa
                AnimatedVisibility(visible = deliveryType == "En mesa") {
                    OutlinedTextField(
                        value = tableNumber,
                        onValueChange = { if (it.all { char -> char.isDigit() }) tableNumber = it },
                        label = { Text("Número de mesa") },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onConfirm(deliveryType, tableNumber.toIntOrNull()) 
                }, 
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = cart.isNotEmpty() && (deliveryType == "Para llevar" || tableNumber.isNotEmpty()),
                shape = RoundedCornerShape(12.dp)
            ) { 
                Text("Confirmar Compra") 
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Seguir comprando", color = Color.Gray) }
        }
    )
}
