package com.example.sakura_fusion.data.detalle_pedido

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sakura_fusion.data.pedido.Pedido
import com.example.sakura_fusion.data.producto.Producto
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "detalle_pedido",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id_pedido"],
            childColumns = ["id_pedido"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id_producto"],
            childColumns = ["id_producto"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DetallePedido(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_detalle")
    @SerializedName("id_detalle")
    val idDetalle: Int = 0,

    val cantidad: Int,

    @ColumnInfo(name = "precio_unitario")
    @SerializedName("precio_unitario")
    val precioUnitario: Double,

    @ColumnInfo(name = "id_pedido")
    @SerializedName("id_pedido")
    val idPedido: Int,

    @ColumnInfo(name = "id_producto")
    @SerializedName("id_producto")
    val idProducto: Int
)
