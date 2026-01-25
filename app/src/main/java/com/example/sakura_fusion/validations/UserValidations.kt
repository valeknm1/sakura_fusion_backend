package com.example.sakura_fusion.validations

import android.util.Patterns

/**
 * IE 2.1.1: Lógica de validación aplicada en los formularios.
 * Se centralizan las reglas de negocio para asegurar la integridad de los datos
 * antes de su procesamiento o almacenamiento.
 */
object UserValidations {

    // Justificación: Uso de Patterns.EMAIL_ADDRESS para asegurar un formato de correo electrónico estándar.
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Justificación: Validación específica para telefonía móvil en Chile (9 dígitos, comienza con 9).
    fun isValidTelefono(telefono: String): Boolean {
        return telefono.length == 9 && telefono.startsWith("9") && telefono.all { it.isDigit() }
    }

    // Justificación: Requisito mínimo de seguridad para contraseñas de usuarios.
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * IE 2.1.1: Integración de nuevas condiciones.
     * Se verifica la existencia del correo en la lista de usuarios para evitar duplicados.
     */
    fun isEmailAlreadyRegistered(email: String, existingEmails: List<String>): Boolean {
        return existingEmails.any { it.equals(email.trim(), ignoreCase = true) }
    }
}
