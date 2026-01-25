package com.example.sakura_fusion.data.pedido

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sakura_fusion.data.usuario.Usuario
import com.example.sakura_fusion.data.reserva.Reserva
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "pedido",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Reserva::class,
            parentColumns = ["id_reserva"],
            childColumns = ["id_reserva"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pedido")
    @SerializedName("id_pedido")
    val idPedido: Int = 0,

    val fecha: String,
    val estado: String = "Pendiente",
    val total: Double,

    @ColumnInfo(name = "id_usuario")
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_reserva")
    @SerializedName("id_reserva")
    val idReserva: Int? = null,

    val tipoEntrega: String = "Para llevar",
    val numeroMesa: Int? = null,
    
    @SerializedName("nombreCliente")
    val nombreCliente: String = "Cliente Anónimo"
)
