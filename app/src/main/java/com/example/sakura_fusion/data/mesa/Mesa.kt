package com.example.sakura_fusion.data.mesa

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "mesa")
data class Mesa(
    @PrimaryKey
    @ColumnInfo(name = "id_mesa")
    @SerializedName("id_mesa")
    val idMesa: Int,
    val numero: Int,
    val capacidad: Int,
    val disponible: Boolean
)
