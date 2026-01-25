package com.example.sakura_fusion.data.reserva

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.data.mesa.Mesa
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "reserva",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Mesa::class,
            parentColumns = ["id_mesa"],
            childColumns = ["id_mesa"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reserva(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_reserva")
    @SerializedName("id_reserva")
    val idReserva: Int = 0,

    val fecha: String,
    val hora: String,

    @ColumnInfo(name = "cant_personas")
    @SerializedName("cant_personas")
    val cantPersonas: Int,

    val estado: String = "Pendiente",

    @ColumnInfo(name = "id_usuario")
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_mesa")
    @SerializedName("id_mesa")
    val idMesa: Int,

    @SerializedName("nombreCliente")
    val nombreCliente: String = "Cliente Anónimo"
)
