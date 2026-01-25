package com.example.sakura_fusion.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.reserva.Reserva
import com.example.sakura_fusion.validations.ReservationValidations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(
    idUsuario: Int, 
    allReservations: List<Reserva>,
    onNewReserva: (Int) -> Unit
) {
    var showInitialDialog by remember { mutableStateOf(false) }
    var peopleCount by remember { mutableStateOf("") }
    
    val userReservations = allReservations.filter { it.idUsuario == idUsuario }.sortedByDescending { it.idReserva }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Reservas", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showInitialDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Reservar") }
            )
        }
    ) { padding ->
        if (userReservations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(8.dp))
                    Text("No tienes reservas aún.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(userReservations, key = { it.idReserva }) { reserva ->
                    ReservaCard(reserva)
                }
            }
        }

        if (showInitialDialog) {
            val count = peopleCount.toIntOrNull() ?: 0
            val isCountValid = peopleCount.isEmpty() || ReservationValidations.isValidPersonCount(count)

            AlertDialog(
                onDismissRequest = { showInitialDialog = false },
                title = { Text("¿Para cuántas personas?") },
                text = {
                    Column {
                        Text("Indica el tamaño de tu grupo (máximo ${ReservationValidations.MAX_PERSONS} personas).", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = peopleCount,
                            onValueChange = { if (it.all { c -> c.isDigit() }) peopleCount = it },
                            label = { Text("N° de personas") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            isError = !isCountValid,
                            supportingText = {
                                if (!isCountValid) Text("Debe ser entre 1 y ${ReservationValidations.MAX_PERSONS}")
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            if (ReservationValidations.isValidPersonCount(count)) {
                                showInitialDialog = false
                                onNewReserva(count)
                            }
                        },
                        enabled = peopleCount.isNotEmpty() && ReservationValidations.isValidPersonCount(count)
                    ) {
                        Text("Ver Mesas")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInitialDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun ReservaCard(reserva: Reserva) {
    val isConfirmada = reserva.estado == "Confirmada"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConfirmada) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isConfirmada) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isConfirmada) Color(0xFF388E3C) else Color(0xFFE65100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = reserva.estado.uppercase(),
                        color = if (isConfirmada) Color(0xFF388E3C) else Color(0xFFE65100),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(text = "Fecha: ${reserva.fecha}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = reserva.hora, 
                    fontSize = 24.sp, 
                    color = MaterialTheme.colorScheme.primary, 
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${reserva.cantPersonas} personas - Mesa #${reserva.idMesa}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
