package org.delcom.entities


import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Bag(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var merek: String,
    var harga: Double,
    var warna: String,
    var bahan: String,
    var stok: Int,
    var deskripsi: String,
    var pathGambar: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)