package com.example.sakura_fusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.ui.viewmodel.AppViewModel
import com.example.sakura_fusion.validations.UserValidations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    appViewModel: AppViewModel,
    onBack: () -> Unit, 
    onRegisterSuccess: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // IE 2.1.2: Validaciones en tiempo real
    val isNombreValid = nombre.isEmpty() || nombre.length >= 3
    val isEmailValid = email.isEmpty() || UserValidations.isValidEmail(email)
    val isTelefonoValid = telefono.isEmpty() || UserValidations.isValidTelefono(telefono)
    val isPasswordValid = password.isEmpty() || UserValidations.isValidPassword(password)
    
    val isFormValid = nombre.length >= 3 && 
                      UserValidations.isValidEmail(email) && 
                      UserValidations.isValidTelefono(telefono) && 
                      UserValidations.isValidPassword(password)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Únete a Sakura Fusion hoy mismo",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorMessage = null },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !isNombreValid,
                supportingText = {
                    if (!isNombreValid) Text("El nombre es demasiado corto", color = MaterialTheme.colorScheme.error)
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !isEmailValid,
                supportingText = {
                    if (!isEmailValid) Text("Formato de correo inválido", color = MaterialTheme.colorScheme.error)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { if (it.length <= 9) { telefono = it; errorMessage = null } },
                label = { Text("Teléfono móvil") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !isTelefonoValid,
                supportingText = {
                    if (!isTelefonoValid) Text("Debe comenzar con 9 y tener 9 dígitos", color = MaterialTheme.colorScheme.error)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                prefix = { Text("+56 ") },
                placeholder = { Text("912345678") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !isPasswordValid,
                supportingText = {
                    if (!isPasswordValid) Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.error)
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        if (appViewModel.isEmailRegistered(email)) {
                            errorMessage = "Este correo ya está registrado en nuestra base de datos."
                            isLoading = false
                        } else {
                            val nuevoUsuario = Usuario(
                                idUsuario = 0,
                                nombre = nombre,
                                email = email.lowercase().trim(),
                                password = password,
                                idRol = 3 // Rol Cliente
                            )
                            onRegisterSuccess(nuevoUsuario)
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && isFormValid,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White, 
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
