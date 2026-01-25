package com.example.sakura_fusion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sakura_fusion.data.rol.Rol
import com.example.sakura_fusion.data.rol.RolDao
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.data.usuario.UsuarioDao
import com.example.sakura_fusion.data.categoria.Categoria
import com.example.sakura_fusion.data.categoria.CategoriaDao
import com.example.sakura_fusion.data.producto.Producto
import com.example.sakura_fusion.data.producto.ProductoDao
import com.example.sakura_fusion.data.mesa.Mesa
import com.example.sakura_fusion.data.mesa.MesaDao
import com.example.sakura_fusion.data.reserva.Reserva
import com.example.sakura_fusion.data.reserva.ReservaDao
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.pedido.PedidoDao
import com.example.sakura_fusion.data.detalle_pedido.DetallePedido
import com.example.sakura_fusion.data.detalle_pedido.DetallePedidoDao

@Database(
    entities = [
        Rol::class,
        Usuario::class,
        Categoria::class,
        Producto::class,
        Mesa::class,
        Reserva::class,
        Pedido::class,
        DetallePedido::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rolDao(): RolDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun mesaDao(): MesaDao
    abstract fun reservaDao(): ReservaDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detallePedidoDao(): DetallePedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sakura_fusion"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
