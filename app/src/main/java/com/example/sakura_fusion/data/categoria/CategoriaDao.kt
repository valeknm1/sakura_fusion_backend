package com.example.sakura_fusion.data.categoria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria)

    @Query("SELECT * FROM categoria")
    suspend fun getAll(): List<Categoria>
}
