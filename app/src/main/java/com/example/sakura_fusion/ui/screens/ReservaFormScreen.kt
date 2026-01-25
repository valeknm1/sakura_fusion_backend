package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaFormScreen(onBack: (String, String, String) -> Unit) {
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Generar opciones de días (Hoy + 7 días)
    val availableDates = remember {
        val dates = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        for (i in 0..7) {
            dates.add(sdf.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        dates
    }

    // Generar opciones de horas (12:00 PM a 11:00 PM)
    val availableHours = remember {
        val hours = mutableListOf<String>()
        for (h in 12..23) {
            hours.add(String.format("%02d:00", h))
            if (h != 23) hours.add(String.format("%02d:30", h))
        }
        hours
    }

    var dateExpanded by remember { mutableStateOf(false) }
    var hourExpanded by remember { mutableStateOf(false) }

    // El campo de personas se elimina de aquí porque ya se eligió antes
    val isFormValid = fecha.isNotEmpty() && hora.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Reserva") },
                navigationIcon = {
                    IconButton(onClick = { onBack("", "", "") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Selecciona el horario de tu visita", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
            
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            // Selector de Fecha (Dropdown)
            ExposedDropdownMenuBox(
                expanded = dateExpanded,
                onExpandedChange = { dateExpanded = !dateExpanded }
            ) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Día") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Elige una fecha") }
                )
                ExposedDropdownMenu(
                    expanded = dateExpanded,
                    onDismissRequest = { dateExpanded = false }
                ) {
                    availableDates.forEach { selectionDate ->
                        DropdownMenuItem(
                            text = { Text(selectionDate) },
                            onClick = {
                                fecha = selectionDate
                                dateExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Hora (Dropdown)
            ExposedDropdownMenuBox(
                expanded = hourExpanded,
                onExpandedChange = { hourExpanded = !hourExpanded }
            ) {
                OutlinedTextField(
                    value = hora,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Hora") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hourExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Elige una hora") }
                )
                ExposedDropdownMenu(
                    expanded = hourExpanded,
                    onDismissRequest = { hourExpanded = false }
                ) {
                    availableHours.forEach { selectionHour ->
                        DropdownMenuItem(
                            text = { Text(selectionHour) },
                            onClick = {
                                hora = selectionHour
                                hourExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isFormValid) {
                        scope.launch {
                            isLoading = true
                            // Se pasa una cadena vacía para 'personas' ya que se usa el valor persistido
                            onBack(fecha, hora, "")
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && isFormValid
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Confirmar Reserva")
            }
        }
    }
}
