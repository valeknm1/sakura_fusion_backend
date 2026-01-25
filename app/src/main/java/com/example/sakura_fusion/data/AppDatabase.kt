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

/**
 * IE 2.3.2: Estructura arquitectónica y almacenamiento local.
 * Se utiliza el patrón Singleton para la instancia de la base de datos,
 * asegurando un único punto de acceso y favoreciendo la mantenibilidad.
 */
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
    // IE 2.3.1: Se incrementa la versión a 3 debido a la modificación del esquema 
    // (adición de imagenUri y telefono en la entidad Usuario).
    version = 3, 
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

        /**
         * IE 2.3.1: Demostración de implementación de almacenamiento local.
         * Recuperación funcional de la instancia de base de datos Room.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sakura_fusion"
                )
                // Se utiliza fallbackToDestructiveMigration para facilitar el desarrollo 
                // ante cambios frecuentes de esquema en esta etapa del proyecto.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
