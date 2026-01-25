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

    // IE 2.1.2: Validación de correo duplicado
    fun isEmailAlreadyRegistered(email: String, existingEmails: List<String>): Boolean {
        return existingEmails.any { it.equals(email.trim(), ignoreCase = true) }
    }
}
