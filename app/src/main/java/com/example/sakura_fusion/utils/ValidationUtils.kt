package com.example.sakura_fusion.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Locale

object ValidationUtils {

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

    fun validateLogin(email: String, password: String): String? {
        return when {
            email == "admin@sakura.com" && password == "admin123" -> "admin"
            email == "mesero@sakura.com" && password == "mesero123" -> "mesero"
            isValidEmail(email) && !email.contains("@sakura.com") && isValidPassword(password) -> "cliente"
            else -> null
        }
    }

    fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isValidTime(time: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isValidPersonCount(count: String): Boolean {
        val num = count.toIntOrNull()
        return num != null && num in 1..20
    }
    
    fun isValidProductName(name: String): Boolean = name.length >= 3
    fun isValidProductDescription(desc: String): Boolean = desc.length >= 10
    fun isValidPrice(price: String): Boolean = price.toDoubleOrNull()?.let { it > 0 } ?: false
    fun isValidStock(stock: String): Boolean = stock.toIntOrNull()?.let { it >= 0 } ?: false
}
