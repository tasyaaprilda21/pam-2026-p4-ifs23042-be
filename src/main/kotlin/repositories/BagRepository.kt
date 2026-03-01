package org.delcom.repositories

import org.delcom.dao.BagDAO
import org.delcom.entities.Bag
import org.delcom.helpers.bagDaoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.BagTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class BagRepository : IBagRepository {
    override suspend fun getBags(search: String): List<Bag> = suspendTransaction {
        if (search.isBlank()) {
            BagDAO.all()
                .orderBy(BagTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::bagDaoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            BagDAO
                .find { BagTable.nama.lowerCase() like keyword }
                .orderBy(BagTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::bagDaoToModel)
        }
    }

    override suspend fun getBagById(id: String): Bag? = suspendTransaction {
        BagDAO
            .find { BagTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::bagDaoToModel)
            .firstOrNull()
    }

    override suspend fun getBagByName(name: String): Bag? = suspendTransaction {
        BagDAO
            .find { BagTable.nama eq name }
            .limit(1)
            .map(::bagDaoToModel)
            .firstOrNull()
    }

    override suspend fun addBag(bag: Bag): String = suspendTransaction {
        val bagDAO = BagDAO.new {
            nama = bag.nama
            merek = bag.merek
            harga = bag.harga
            warna = bag.warna
            bahan = bag.bahan
            stok = bag.stok
            deskripsi = bag.deskripsi
            pathGambar = bag.pathGambar
            createdAt = bag.createdAt
            updatedAt = bag.updatedAt
        }
        bagDAO.id.value.toString()
    }

    override suspend fun updateBag(id: String, newBag: Bag): Boolean = suspendTransaction {
        val bagDAO = BagDAO
            .find { BagTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (bagDAO != null) {
            bagDAO.nama = newBag.nama
            bagDAO.merek = newBag.merek
            bagDAO.harga = newBag.harga
            bagDAO.warna = newBag.warna
            bagDAO.bahan = newBag.bahan
            bagDAO.stok = newBag.stok
            bagDAO.deskripsi = newBag.deskripsi
            bagDAO.pathGambar = newBag.pathGambar
            bagDAO.updatedAt = newBag.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeBag(id: String): Boolean = suspendTransaction {
        val rowsDeleted = BagTable.deleteWhere {
            BagTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}