package com.example.sakura_fusion.validations

import java.text.SimpleDateFormat
import java.util.Locale

object ReservationValidations {

    const val MAX_PERSONS = 8 // Actualizado a 8 según requerimiento

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

    fun isValidPersonCount(count: Int): Boolean {
        return count in 1..MAX_PERSONS
    }
}
