package com.example.sakura_fusion.data.producto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sakura_fusion.data.categoria.Categoria
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "producto",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_producto")
    @SerializedName("id_producto")
    val idProducto: Int = 0,

    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,

    @ColumnInfo(name = "id_categoria")
    @SerializedName("id_categoria")
    val idCategoria: Int,

    @SerializedName("imagenUrl")
    val imagenUrl: String? = null,

    @SerializedName("nombreCategoria")
    val nombreCategoria: String = "General"
)
