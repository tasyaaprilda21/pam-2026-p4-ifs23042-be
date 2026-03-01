package org.delcom.dao

import org.delcom.tables.BagTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class BagDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, BagDAO>(BagTable)

    var nama by BagTable.nama
    var merek by BagTable.merek
    var harga by BagTable.harga
    var warna by BagTable.warna
    var bahan by BagTable.bahan
    var stok by BagTable.stok
    var deskripsi by BagTable.deskripsi
    var pathGambar by BagTable.pathGambar
    var createdAt by BagTable.createdAt
    var updatedAt by BagTable.updatedAt
}