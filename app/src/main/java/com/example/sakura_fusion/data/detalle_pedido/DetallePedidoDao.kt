package com.example.sakura_fusion.data.detalle_pedido

import androidx.room.*

@Dao
interface DetallePedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detalle: DetallePedido)

    @Query("SELECT * FROM detalle_pedido WHERE id_pedido = :idPedido")
    suspend fun getByPedido(idPedido: Int): List<DetallePedido>

    @Delete
    suspend fun delete(detalle: DetallePedido)
}
