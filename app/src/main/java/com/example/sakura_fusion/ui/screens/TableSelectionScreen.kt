package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.mesa.Mesa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableSelectionScreen(
    peopleCount: Int,
    onTableSelected: (Mesa) -> Unit, 
    onBack: () -> Unit
) {
    var selectedMesa by remember { mutableStateOf<Mesa?>(null) }
    
    // Mock tables usando la entidad de data
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

    val filteredMesas = mesas.filter { it.capacidad >= peopleCount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona tu Mesa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Reserva para:", fontWeight = FontWeight.Bold)
                        Text("$peopleCount personas", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Text(
                "Mesas recomendadas para tu grupo:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (filteredMesas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay mesas disponibles para $peopleCount personas.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredMesas) { mesa ->
                        TableCard(
                            mesa = mesa,
                            isSelected = selectedMesa?.idMesa == mesa.idMesa,
                            onClick = { if (mesa.disponible) selectedMesa = mesa }
                        )
                    }
                }

                Button(
                    onClick = { selectedMesa?.let { onTableSelected(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    enabled = selectedMesa != null,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Finalizar Reserva - Mesa ${selectedMesa?.numero ?: ""}")
                }
            }
        }
    }
}

@Composable
fun TableCard(mesa: Mesa, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = mesa.disponible) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                !mesa.disponible -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = if (isSelected) CardDefaults.cardElevation(8.dp) else CardDefaults.cardElevation(2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Mesa ${mesa.numero}",
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Capacidad: ${mesa.capacidad}",
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.secondary
                )
                if (!mesa.disponible) {
                    Text("No disponible", fontSize = 10.sp, color = Color.Red)
                }
            }
        }
    }
}
