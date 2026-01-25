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
        return existingEmails.contains(email.lowercase())
    }

    fun validateLoginRole(email: String, password: String): String? {
        return when {
            // Admin: email y password exactos
            email == "admin@sakura.com" && password == "admin123" -> "admin"
            
            // Mesero: email y password exactos
            email == "mesero@sakura.com" && password == "mesero123" -> "mesero"
            
            // Cliente: email válido, no del staff, y password de al menos 6 chars
            isValidEmail(email) && !email.endsWith("@sakura.com") && isValidPassword(password) -> "cliente"
            
            // Caso de error: credenciales no coinciden
            else -> null
        }
    }
}
