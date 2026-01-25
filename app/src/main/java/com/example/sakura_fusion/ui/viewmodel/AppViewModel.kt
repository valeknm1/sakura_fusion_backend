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

/**
 * AppViewModel: El "cerebro" de la aplicación.
 * Gestiona los datos globales, la lógica de negocio y el estado de la UI.
 * Implementa el patrón MVVM.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    // Repositorio para interactuar con la base de datos Room
    private val repository = SakuraRepository(application)

    // Estados reactivos (StateFlow) para que las pantallas se actualicen automáticamente
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _mesas = MutableStateFlow<List<Mesa>>(emptyList())
    val mesas: StateFlow<List<Mesa>> = _mesas.asStateFlow()

    init {
        // Cargar datos al iniciar la aplicación
        loadInitialData()
    }

    /**
     * loadInitialData: Configura los datos base si la app es nueva.
     * Crea roles, usuarios iniciales, categorías y mesas.
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            // IE 2.3.1: Asegurar datos estructurales siempre al inicio
            repository.insertRol(Rol(1, "Administrador"))
            repository.insertRol(Rol(2, "Mesero"))
            repository.insertRol(Rol(3, "Cliente"))

            // Crear usuarios predeterminados si no existen
            if (repository.getUsuarioByEmail("admin@sakura.com") == null) {
                repository.insertUsuario(Usuario(1, "Admin Sakura", "admin@sakura.com", "admin123", "", null, 1))
            }
            if (repository.getUsuarioByEmail("mesero@sakura.com") == null) {
                repository.insertUsuario(Usuario(2, "Mesero Sakura", "mesero@sakura.com", "mesero123", "", null, 2))
            }
            if (repository.getUsuarioByEmail("valeria@gmail.com") == null) {
                repository.insertUsuario(Usuario(3, "Valeria", "valeria@gmail.com", "valeria123", "912345678", null, 3))
            }

            // Inicializar mesas (1 a 8)
            val existingTables = repository.getAllMesas()
            if (existingTables.isEmpty()) {
                for (i in 1..8) {
                    repository.insertMesa(Mesa(i, i, if (i % 2 == 0) 4 else 2, true))
                }
            } else {
                // IE 2.1.2: Forzar disponibilidad de mesas específicas para el demo
                val mesa2 = existingTables.find { it.numero == 2 }
                if (mesa2 != null && !mesa2.disponible) repository.updateMesa(mesa2.copy(disponible = true))
                val mesa3 = existingTables.find { it.numero == 3 }
                if (mesa3 != null && !mesa3.disponible) repository.updateMesa(mesa3.copy(disponible = true))
            }

            // Crear productos iniciales en el menú
            val existingProducts = repository.getAllProductos()
            if (existingProducts.isEmpty()) {
                repository.insertCategoria(Categoria(1, "Entradas"))
                repository.insertCategoria(Categoria(2, "Sushi"))
                repository.insertCategoria(Categoria(3, "Ramen"))
                repository.insertCategoria(Categoria(4, "Bebidas"))
                repository.insertCategoria(Categoria(5, "Postres"))

                val mockProducts = listOf(
                    Producto(1, "Sushi Sakura", "Salmón y aguacate", 8990.0, 10, 2, "https://plus.unsplash.com/premium_photo-1668143358351-b20146dbcc02?q=80&w=500&auto=format&fit=crop", "Sushi"),
                    Producto(2, "Ramen Tonkotsu", "Cerdo y fideos", 11500.0, 5, 3, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?q=80&w=500&auto=format&fit=crop", "Ramen"),
                    Producto(3, "Gyoza", "Empanadillas japonesas", 5500.0, 20, 1, "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?q=80&w=500&auto=format&fit=crop", "Entradas"),
                    Producto(4, "Mochi Fresa", "Postre de arroz", 3200.0, 15, 5, "https://images.unsplash.com/photo-1563805042-7684c019e1cb?q=80&w=500&auto=format&fit=crop", "Postres")
                )
                mockProducts.forEach { repository.insertProducto(it) }
            } else {
                // IE 2.1.2: Corrección automática de URLs de imágenes
                val ramen = existingProducts.find { it.nombre.contains("Ramen", ignoreCase = true) }
                if (ramen != null && (ramen.imagenUrl == null || ramen.imagenUrl.contains("photo-1557872245"))) {
                    repository.updateProducto(ramen.copy(imagenUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?q=80&w=500&auto=format&fit=crop"))
                }
                
                // Actualizar imagen de Mochi por una real de internet
                val mochi = existingProducts.find { it.nombre.contains("Mochi", ignoreCase = true) }
                if (mochi != null && (mochi.imagenUrl == null || mochi.imagenUrl == "mochi_fresa")) {
                    repository.updateProducto(mochi.copy(imagenUrl = "https://images.unsplash.com/photo-1563805042-7684c019e1cb?q=80&w=500&auto=format&fit=crop"))
                }
            }

            // Sincronizar todos los datos con la UI
            refreshData()
        }
    }

    /**
     * refreshData: Recupera los datos más actuales de la base de datos Room.
     */
    private suspend fun refreshData() {
        _productos.value = repository.getAllProductos()
        _reservas.value = repository.getAllReservas()
        _pedidos.value = repository.getAllPedidos()
        _mesas.value = repository.getAllMesas()
    }

    /**
     * login: Valida credenciales de acceso.
     */
    suspend fun login(email: String, pass: String): Usuario? {
        val cleanEmail = email.lowercase().trim()
        val user = repository.getUsuarioByEmail(cleanEmail)
        if (pass.isEmpty()) return user
        return if (user != null && user.password == pass) user else null
    }

    /**
     * isEmailRegistered: Verifica si un correo ya existe en la base de datos.
     */
    suspend fun isEmailRegistered(email: String): Boolean {
        return repository.getUsuarioByEmail(email.lowercase().trim()) != null
    }

    /**
     * registrarUsuario: Guarda un nuevo cliente en la base de datos local.
     */
    fun registrarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.insertUsuario(usuario.copy(email = usuario.email.lowercase().trim()))
            refreshData()
        }
    }

    /**
     * resetPassword: Cambia la clave de un usuario existente.
     */
    fun resetPassword(email: String, newPass: String) {
        viewModelScope.launch {
            val user = repository.getUsuarioByEmail(email.lowercase().trim())
            if (user != null) {
                repository.updateUsuario(user.copy(password = newPass))
                refreshData()
            }
        }
    }

    /**
     * updateProfile: Actualiza los datos personales (Nombre, Teléfono, Pass) del perfil.
     */
    fun updateProfile(email: String, nuevoNombre: String, nuevoTelefono: String, nuevaPass: String) {
        viewModelScope.launch {
            val user = repository.getUsuarioByEmail(email.lowercase().trim())
            if (user != null) {
                val updatedUser = user.copy(
                    nombre = if (nuevoNombre.isNotEmpty()) nuevoNombre else user.nombre,
                    telefono = if (nuevoTelefono.isNotEmpty()) nuevoTelefono else user.telefono,
                    password = if (nuevaPass.isNotEmpty()) nuevaPass else user.password
                )
                repository.updateUsuario(updatedUser)
                refreshData()
            }
        }
    }

    /**
     * updateProfileImage: Guarda la foto elegida por el usuario en Room.
     */
    fun updateProfileImage(email: String, uri: String) {
        viewModelScope.launch {
            val user = repository.getUsuarioByEmail(email.lowercase().trim())
            if (user != null) {
                repository.updateUsuario(user.copy(imagenUri = uri))
                refreshData()
            }
        }
    }

    /**
     * crearPedido: Registra una nueva orden de comida.
     * Incluye una simulación de tiempo de cocina (60s).
     */
    fun crearPedido(pedido: Pedido) {
        viewModelScope.launch {
            val id = repository.insertPedido(pedido)
            refreshData()
            
            // Simulación: Cambiar a "Listo" automáticamente después de 1 minuto
            delay(60000) 
            val p = repository.getAllPedidos().find { it.idPedido == id.toInt() }
            if (p != null) {
                repository.updatePedido(p.copy(estado = "Listo"))
                refreshData()
            }
        }
    }

    /**
     * entregarPedido: El mesero marca que el plato ya está servido.
     */
    fun entregarPedido(pedido: Pedido) {
        viewModelScope.launch {
            repository.updatePedido(pedido.copy(estado = "Entregado"))
            refreshData()
        }
    }

    /**
     * crearReserva: Registra una nueva reservación de mesa.
     */
    fun crearReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.insertReserva(reserva)
            refreshData()
        }
    }

    /**
     * confirmarReserva: El administrador aprueba la reserva del cliente.
     */
    fun confirmarReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.updateReserva(reserva.copy(estado = "Confirmada"))
            refreshData()
        }
    }

    /**
     * clearAllOrders: Elimina todo el historial de pedidos (útil para el admin).
     */
    fun clearAllOrders() {
        viewModelScope.launch {
            val db = com.example.sakura_fusion.data.AppDatabase.getInstance(getApplication())
            db.pedidoDao().deleteAll()
            refreshData()
        }
    }
}
