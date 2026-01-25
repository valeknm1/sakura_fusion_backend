package com.example.sakura_fusion.data.usuario

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.sakura_fusion.data.rol.Rol
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "usuario",
    foreignKeys = [
        ForeignKey(
            entity = Rol::class,
            parentColumns = ["id_rol"],
            childColumns = ["id_rol"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    @SerializedName("id_usuario")
    val idUsuario: Int = 0,

    val nombre: String,
    val email: String,
    val password: String,
    
    val telefono: String = "",
    
    @SerializedName("imagen_uri")
    val imagenUri: String? = null,

    @ColumnInfo(name = "id_rol")
    @SerializedName("id_rol")
    val idRol: Int
)
