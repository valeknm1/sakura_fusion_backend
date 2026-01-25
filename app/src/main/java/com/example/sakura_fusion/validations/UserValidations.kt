package com.example.sakura_fusion.validations

import android.util.Patterns

object UserValidations {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidTelefono(telefono: String): Boolean {
        return telefono.length == 9 && telefono.startsWith("9") && telefono.all { it.isDigit() }
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isEmailAlreadyRegistered(email: String, existingEmails: List<String>): Boolean {
        return existingEmails.any { it.equals(email.trim(), ignoreCase = true) }
    }

    fun validateLoginRole(email: String, password: String): String? {
        val cleanEmail = email.lowercase().trim()
        return when {
            // Admin: email y password exactos
            cleanEmail == "admin@sakura.com" && password == "admin123" -> "admin"
            
            // Mesero: email y password exactos
            cleanEmail == "mesero@sakura.com" && password == "mesero123" -> "mesero"
            
            // Cliente Demo: valeria@gmail.com con su clave específica
            cleanEmail == "valeria@gmail.com" && password == "valeria123" -> "cliente"
            
            // Fallback para otros clientes registrados en el demo
            isValidEmail(cleanEmail) && !cleanEmail.endsWith("@sakura.com") && password == "123456" -> "cliente"
            
            // Caso de error: credenciales no coinciden o no cumplen requisitos
            else -> null
        }
    }
}
