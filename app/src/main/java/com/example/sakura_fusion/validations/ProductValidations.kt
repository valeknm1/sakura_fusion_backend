package com.example.sakura_fusion.validations

object ProductValidations {
    fun isValidName(name: String): Boolean = name.length >= 3
    fun isValidDescription(desc: String): Boolean = desc.length >= 10
    fun isValidPrice(price: Double): Boolean = price > 0
    fun isValidStock(stock: Int): Boolean = stock >= 0
}
