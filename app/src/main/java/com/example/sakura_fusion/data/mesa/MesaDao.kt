package com.example.sakura_fusion.data.mesa

import androidx.room.*

@Dao
interface MesaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mesa: Mesa)

    @Query("SELECT * FROM mesa")
    suspend fun getAll(): List<Mesa>

    @Update
    suspend fun update(mesa: Mesa)
}
