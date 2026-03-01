package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Bag

@Serializable
data class BagRequest(
    var nama: String = "",
    var merek: String = "",
    var harga: Double = 0.0,
    var warna: String = "",
    var bahan: String = "",
    var stok: Int = 0,
    var deskripsi: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "merek" to merek,
            "harga" to harga,
            "warna" to warna,
            "bahan" to bahan,
            "stok" to stok,
            "deskripsi" to deskripsi,
            "pathGambar" to pathGambar,
        )
    }

    fun toEntity(): Bag {
        return Bag(
            nama = nama,
            merek = merek,
            harga = harga,
            warna = warna,
            bahan = bahan,
            stok = stok,
            deskripsi = deskripsi,
            pathGambar = pathGambar,
        )
    }
}