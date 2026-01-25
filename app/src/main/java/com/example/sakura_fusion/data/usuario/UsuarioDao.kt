package com.example.sakura_fusion.data.usuario

import androidx.room.*

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuario")
    suspend fun getAll(): List<Usuario>

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): Usuario?

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)
}
