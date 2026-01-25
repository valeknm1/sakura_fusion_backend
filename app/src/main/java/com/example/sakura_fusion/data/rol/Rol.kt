package com.example.sakura_fusion.data.rol

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "rol")
data class Rol(
    @PrimaryKey
    @ColumnInfo(name = "id_rol")
    @SerializedName("id_rol") 
    val idRol: Int,
    
    @ColumnInfo(name = "nombre_rol")
    @SerializedName("nombre_rol") 
    val nombreRol: String
)
