package com.example.sakura_fusion.data.producto

import androidx.room.*

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    @Query("SELECT * FROM producto")
    suspend fun getAll(): List<Producto>

    @Query("SELECT * FROM producto WHERE id_categoria = :idCategoria")
    suspend fun getByCategoria(idCategoria: Int): List<Producto>

    @Update
    suspend fun update(producto: Producto)

    @Delete
    suspend fun delete(producto: Producto)
}
