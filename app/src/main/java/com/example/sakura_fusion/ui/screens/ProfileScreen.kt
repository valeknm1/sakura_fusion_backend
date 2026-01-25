package com.example.sakura_fusion.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userEmail: String, onEditProfile: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    val fileProviderAuthority = "${context.packageName}.fileprovider"
    
    val tempUri = remember {
        try {
            val file = File(context.cacheDir, "temp_image.jpg")
            if (!file.exists()) file.createNewFile()
            FileProvider.getUriForFile(context, fileProviderAuthority, file)
        } catch (e: Exception) {
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) imageUri = tempUri
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && tempUri != null) {
            cameraLauncher.launch(tempUri)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            // Botón de cerrar sesión fijo en la parte inferior de la pantalla de perfil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            TextButton(onClick = { showDialog = true }) {
                Text("Cambiar foto de perfil")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = userEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(text = userEmail, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(32.dp))

            ProfileInfoItem(label = "Rol", value = if (userEmail.contains("admin")) "Administrador" else if (userEmail.contains("mesero")) "Mesero" else "Cliente")
            ProfileInfoItem(label = "Teléfono", value = "+56 9 1234 5678")
            ProfileInfoItem(label = "Miembro desde", value = "Enero 2024")
            
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Datos y Seguridad")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("Elige una de las siguientes opciones para actualizar tu imagen.") },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { 
                            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                if (tempUri != null) cameraLauncher.launch(tempUri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            showDialog = false 
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        enabled = tempUri != null
                    ) {
                        Icon(Icons.Default.CameraAlt, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tomar Foto")
                    }
                    OutlinedButton(
                        onClick = { 
                            galleryLauncher.launch("image/*")
                            showDialog = false 
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Elegir de Galería")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontWeight = FontWeight.Medium)
            Text(text = value, color = MaterialTheme.colorScheme.primary)
        }
    }
}
