package com.example.sakura_fusion.data.pedido

import androidx.room.*

@Dao
interface PedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pedido: Pedido): Long

    @Query("SELECT * FROM pedido")
    suspend fun getAll(): List<Pedido>

    @Query("SELECT * FROM pedido WHERE id_usuario = :idUsuario")
    suspend fun getByUsuario(idUsuario: Int): List<Pedido>

    @Update
    suspend fun update(pedido: Pedido)

    @Delete
    suspend fun delete(pedido: Pedido)
}
