package com.example.sakura_fusion.data

import android.content.Context
import com.example.sakura_fusion.data.rol.Rol
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.data.categoria.Categoria
import com.example.sakura_fusion.data.producto.Producto
import com.example.sakura_fusion.data.mesa.Mesa
import com.example.sakura_fusion.data.reserva.Reserva
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.detalle_pedido.DetallePedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SakuraRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val rolDao = db.rolDao()
    private val usuarioDao = db.usuarioDao()
    private val categoriaDao = db.categoriaDao()
    private val productoDao = db.productoDao()
    private val mesaDao = db.mesaDao()
    private val reservaDao = db.reservaDao()
    private val pedidoDao = db.pedidoDao()
    private val detallePedidoDao = db.detallePedidoDao()

    // Usuarios
    suspend fun insertUsuario(usuario: Usuario) = withContext(Dispatchers.IO) { usuarioDao.insert(usuario) }
    suspend fun getUsuarioByEmail(email: String) = withContext(Dispatchers.IO) { usuarioDao.findByEmail(email) }
    suspend fun getAllUsuarios() = withContext(Dispatchers.IO) { usuarioDao.getAll() }

    // Menú
    suspend fun getAllProductos() = withContext(Dispatchers.IO) { productoDao.getAll() }
    suspend fun insertProducto(producto: Producto) = withContext(Dispatchers.IO) { productoDao.insert(producto) }
    suspend fun deleteProducto(producto: Producto) = withContext(Dispatchers.IO) { productoDao.delete(producto) }
    suspend fun updateProducto(producto: Producto) = withContext(Dispatchers.IO) { productoDao.update(producto) }

    // Categorías
    suspend fun getAllCategorias() = withContext(Dispatchers.IO) { categoriaDao.getAll() }
    suspend fun insertCategoria(categoria: Categoria) = withContext(Dispatchers.IO) { categoriaDao.insert(categoria) }

    // Mesas
    suspend fun getAllMesas() = withContext(Dispatchers.IO) { mesaDao.getAll() }
    suspend fun updateMesa(mesa: Mesa) = withContext(Dispatchers.IO) { mesaDao.update(mesa) }
    suspend fun insertMesa(mesa: Mesa) = withContext(Dispatchers.IO) { mesaDao.insert(mesa) }

    // Reservas
    suspend fun getAllReservas() = withContext(Dispatchers.IO) { reservaDao.getAll() }
    suspend fun insertReserva(reserva: Reserva) = withContext(Dispatchers.IO) { reservaDao.insert(reserva) }
    suspend fun updateReserva(reserva: Reserva) = withContext(Dispatchers.IO) { reservaDao.update(reserva) }

    // Pedidos
    suspend fun getAllPedidos() = withContext(Dispatchers.IO) { pedidoDao.getAll() }
    suspend fun insertPedido(pedido: Pedido): Long = withContext(Dispatchers.IO) { pedidoDao.insert(pedido) }
    suspend fun updatePedido(pedido: Pedido) = withContext(Dispatchers.IO) { pedidoDao.update(pedido) }
    
    // Detalle Pedido
    suspend fun insertDetallePedido(detalle: DetallePedido) = withContext(Dispatchers.IO) { detallePedidoDao.insert(detalle) }
    suspend fun getDetallesByPedido(idPedido: Int) = withContext(Dispatchers.IO) { detallePedidoDao.getByPedido(idPedido) }

    // Roles
    suspend fun insertRol(rol: Rol) = withContext(Dispatchers.IO) { rolDao.insert(rol) }
}
