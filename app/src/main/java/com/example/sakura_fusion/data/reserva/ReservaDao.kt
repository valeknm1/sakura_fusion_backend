package com.example.sakura_fusion.data.reserva

import androidx.room.*

@Dao
interface ReservaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reserva: Reserva)

    @Query("SELECT * FROM reserva")
    suspend fun getAll(): List<Reserva>

    @Query("SELECT * FROM reserva WHERE id_usuario = :idUsuario")
    suspend fun getByUsuario(idUsuario: Int): List<Reserva>

    @Update
    suspend fun update(reserva: Reserva)

    @Delete
    suspend fun delete(reserva: Reserva)
}
