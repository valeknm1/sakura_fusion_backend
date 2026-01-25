package com.example.sakura_fusion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_fusion.data.SakuraRepository
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.producto.Producto
import com.example.sakura_fusion.data.reserva.Reserva
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.data.rol.Rol
import com.example.sakura_fusion.data.categoria.Categoria
import com.example.sakura_fusion.data.mesa.Mesa
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SakuraRepository(application)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // IE 2.3.1: Persistencia de roles y usuarios iniciales
            repository.insertRol(Rol(1, "Administrador"))
            repository.insertRol(Rol(2, "Mesero"))
            repository.insertRol(Rol(3, "Cliente"))

            if (repository.getUsuarioByEmail("admin@sakura.com") == null) {
                repository.insertUsuario(Usuario(1, "Admin Sakura", "admin@sakura.com", "admin123", 1))
            }
            if (repository.getUsuarioByEmail("mesero@sakura.com") == null) {
                repository.insertUsuario(Usuario(2, "Mesero Sakura", "mesero@sakura.com", "mesero123", 2))
            }
            if (repository.getUsuarioByEmail("valeria@gmail.com") == null) {
                repository.insertUsuario(Usuario(3, "Valeria", "valeria@gmail.com", "valeria123", 3))
            }

            val existingProducts = repository.getAllProductos()
            if (existingProducts.isEmpty()) {
                repository.insertCategoria(Categoria(1, "Entradas"))
                repository.insertCategoria(Categoria(2, "Sushi"))
                repository.insertCategoria(Categoria(3, "Ramen"))
                repository.insertCategoria(Categoria(4, "Bebidas"))
                repository.insertCategoria(Categoria(5, "Postres"))

                for (i in 1..8) {
                    repository.insertMesa(Mesa(i, i, if (i % 2 == 0) 4 else 2, true))
                }

                val mockProducts = listOf(
                    Producto(1, "Sushi Sakura", "Salmón y aguacate", 8990.0, 10, 2, "https://plus.unsplash.com/premium_photo-1668143358351-b20146dbcc02?q=80&w=500&auto=format&fit=crop", "Sushi"),
                    Producto(2, "Ramen Tonkotsu", "Cerdo y fideos", 11500.0, 5, 3, "https://images.unsplash.com/photo-1557872245-741f4c666e5c?q=80&w=500&auto=format&fit=crop", "Ramen"),
                    Producto(3, "Gyoza", "Empanadillas japonesas", 5500.0, 20, 1, "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?q=80&w=500&auto=format&fit=crop", "Entradas"),
                    Producto(4, "Mochi Fresa", "Postre de arroz", 3200.0, 15, 5, "https://images.unsplash.com/photo-1563805042-7684c019e1cb?q=80&w=500&auto=format&fit=crop", "Postres")
                )
                mockProducts.forEach { repository.insertProducto(it) }
            }

            refreshData()
        }
    }

    private suspend fun refreshData() {
        _productos.value = repository.getAllProductos()
        _reservas.value = repository.getAllReservas()
        _pedidos.value = repository.getAllPedidos()
    }

    suspend fun login(email: String, pass: String): Usuario? {
        val cleanEmail = email.lowercase().trim()
        val user = repository.getUsuarioByEmail(cleanEmail)
        return if (user != null && user.password == pass) user else null
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return repository.getUsuarioByEmail(email.lowercase().trim()) != null
    }

    fun registrarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.insertUsuario(usuario.copy(email = usuario.email.lowercase().trim()))
            refreshData()
        }
    }

    fun crearPedido(pedido: Pedido) {
        viewModelScope.launch {
            val id = repository.insertPedido(pedido)
            refreshData()
            
            // Simulación: Cambiar a "Listo" después de 1 minuto
            delay(60000) 
            val p = repository.getAllPedidos().find { it.idPedido == id.toInt() }
            if (p != null) {
                repository.updatePedido(p.copy(estado = "Listo"))
                refreshData()
            }
        }
    }

    fun entregarPedido(pedido: Pedido) {
        viewModelScope.launch {
            repository.updatePedido(pedido.copy(estado = "Entregado"))
            refreshData()
        }
    }

    fun crearReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.insertReserva(reserva)
            refreshData()
        }
    }

    fun confirmarReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.updateReserva(reserva.copy(estado = "Confirmada"))
            refreshData()
        }
    }

    fun clearAllOrders() {
        viewModelScope.launch {
            val db = com.example.sakura_fusion.data.AppDatabase.getInstance(getApplication())
            db.pedidoDao().deleteAll()
            refreshData()
        }
    }
}
