package com.example.sakura_fusion.data.categoria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "categoria")
data class Categoria(
    @PrimaryKey
    @ColumnInfo(name = "id_categoria")
    @SerializedName("id_categoria")
    val idCategoria: Int,
    val nombre: String
)
