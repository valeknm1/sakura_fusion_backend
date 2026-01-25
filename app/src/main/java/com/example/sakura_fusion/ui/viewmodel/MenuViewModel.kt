package com.example.sakura_fusion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_fusion.data.SakuraRepository
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.producto.Producto
import com.example.sakura_fusion.ui.screens.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SakuraRepository(application)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _filteredProductos = MutableStateFlow<List<Producto>>(emptyList())
    val filteredProductos: StateFlow<List<Producto>> = _filteredProductos.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        loadProductos()
    }

    private fun loadProductos() {
        viewModelScope.launch {
            try {
                var result = repository.getAllProductos()
                if (result.isEmpty()) {
                    val mockList = listOf(
                        Producto(1, "Sushi Sakura", "Salmón y aguacate", 8990.0, 10, 2, "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?q=80&w=500&auto=format&fit=crop", "Sushi"),
                        Producto(2, "Ramen Tonkotsu", "Cerdo y fideos", 11500.0, 5, 3, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?q=80&w=500&auto=format&fit=crop", "Ramen"),
                        Producto(3, "Gyoza", "Empanadillas japonesas", 5500.0, 20, 1, "https://images.unsplash.com/photo-1534422298391-e4f8c170db06?q=80&w=500&auto=format&fit=crop", "Entradas"),
                        Producto(4, "Mochi Fresa", "Postre de arroz", 3200.0, 15, 5, "https://images.unsplash.com/photo-1582176242273-085d82991a69?q=80&w=500&auto=format&fit=crop", "Postres")
                    )
                    mockList.forEach { repository.insertProducto(it) }
                    result = mockList
                }
                _productos.value = result
            } catch (e: Exception) {
            }
            filterProductos()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterProductos()
    }

    fun onCategoryChange(category: String) {
        _selectedCategory.value = category
        filterProductos()
    }

    private fun filterProductos() {
        _filteredProductos.value = _productos.value.filter {
            val matchesSearch = it.nombre.contains(_searchQuery.value, ignoreCase = true)
            val matchesCategory = _selectedCategory.value == "Todos" || it.nombreCategoria == _selectedCategory.value
            matchesSearch && matchesCategory
        }
    }

    fun addToCart(producto: Producto) {
        val currentCart = _cart.value.toMutableList()
        val existing = currentCart.find { it.producto.idProducto == producto.idProducto }
        if (existing != null) {
            val index = currentCart.indexOf(existing)
            currentCart[index] = existing.copy(cantidad = existing.cantidad + 1)
        } else {
            currentCart.add(CartItem(producto, 1))
        }
        _cart.value = currentCart
    }

    fun removeFromCart(producto: Producto) {
        val currentCart = _cart.value.toMutableList()
        val existing = currentCart.find { it.producto.idProducto == producto.idProducto }
        if (existing != null) {
            if (existing.cantidad > 1) {
                val index = currentCart.indexOf(existing)
                currentCart[index] = existing.copy(cantidad = existing.cantidad - 1)
            } else {
                currentCart.remove(existing)
            }
        }
        _cart.value = currentCart
    }

    fun createPedido(tipoEntrega: String = "Para llevar", numeroMesa: Int? = null): Pedido {
        val total = _cart.value.sumOf { it.producto.precio * it.cantidad }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = sdf.format(Date())
        
        val nuevoPedido = Pedido(
            idPedido = 0, 
            fecha = currentDate,
            estado = "Pendiente",
            total = total,
            idUsuario = 1,
            tipoEntrega = tipoEntrega,
            numeroMesa = numeroMesa,
            nombreCliente = "Cliente Demo"
        )
        
        viewModelScope.launch {
            repository.insertPedido(nuevoPedido)
        }
        
        _cart.value = emptyList()
        return nuevoPedido
    }
}
