package com.example.sakura_fusion.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.ui.viewmodel.AppViewModel
import com.example.sakura_fusion.validations.UserValidations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userEmail: String,
    appViewModel: AppViewModel,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    
    var showNameDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Cargar datos actuales
    LaunchedEffect(userEmail) {
        val user = appViewModel.login(userEmail, "") 
        if (user != null) {
            nombre = user.nombre
            telefono = user.telefono
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Datos Personales", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                "Gestiona tu información",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // IE 2.1.2: Opciones claras para editar cada campo
            EditOptionItem(
                icon = Icons.Default.Person,
                label = "Nombre",
                value = nombre,
                onClick = { showNameDialog = true }
            )
            
            EditOptionItem(
                icon = Icons.Default.Phone,
                label = "Teléfono",
                value = if(telefono.isNotEmpty()) "+56 $telefono" else "No registrado",
                onClick = { showPhoneDialog = true }
            )
            
            EditOptionItem(
                icon = Icons.Default.Lock,
                label = "Contraseña",
                value = "********",
                onClick = { showPasswordDialog = true }
            )
        }
    }

    // Diálogo para cambiar Nombre
    if (showNameDialog) {
        var tempName by remember { mutableStateOf("") } // Iniciamos vacío como solicitó el usuario
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Cambiar Nombre") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nuevo Nombre") },
                    singleLine = true,
                    placeholder = { Text("Escribe tu nuevo nombre") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        nombre = tempName
                        appViewModel.updateProfile(userEmail, tempName, "", "")
                        showNameDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Nombre actualizado") }
                    },
                    enabled = tempName.length >= 3
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showNameDialog = false }) { Text("Cancelar") } }
        )
    }

    // Diálogo para cambiar Teléfono
    if (showPhoneDialog) {
        var tempPhone by remember { mutableStateOf("") } // Iniciamos vacío como solicitó el usuario
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = { Text("Cambiar Teléfono") },
            text = {
                OutlinedTextField(
                    value = tempPhone,
                    onValueChange = { if (it.length <= 9) tempPhone = it },
                    label = { Text("Nuevo Teléfono") },
                    prefix = { Text("+56 ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    placeholder = { Text("912345678") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        telefono = tempPhone
                        appViewModel.updateProfile(userEmail, "", tempPhone, "")
                        showPhoneDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Teléfono actualizado") }
                    },
                    enabled = UserValidations.isValidTelefono(tempPhone)
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showPhoneDialog = false }) { Text("Cancelar") } }
        )
    }

    // Diálogo para cambiar Contraseña
    if (showPasswordDialog) {
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Cambiar Contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Nueva Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        placeholder = { Text("Mínimo 6 caracteres") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirmar Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        placeholder = { Text("Repite la contraseña") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        appViewModel.updateProfile(userEmail, "", "", newPass)
                        showPasswordDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Contraseña actualizada") }
                    },
                    enabled = UserValidations.isValidPassword(newPass) && newPass == confirmPass
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun EditOptionItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
