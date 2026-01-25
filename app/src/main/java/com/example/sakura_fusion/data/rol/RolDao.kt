package com.example.sakura_fusion.data.rol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rol: Rol)

    @Query("SELECT * FROM rol")
    suspend fun getAll(): List<Rol>
}
