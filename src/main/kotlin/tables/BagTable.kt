package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object BagTable : UUIDTable("bags") {
    val nama = varchar("nama", 100)
    val merek = varchar("merek", 100)
    val harga = double("harga")
    val warna = varchar("warna", 50)
    val bahan = varchar("bahan", 100)
    val stok = integer("stok")
    val deskripsi = text("deskripsi")
    val pathGambar = varchar("path_gambar", 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}