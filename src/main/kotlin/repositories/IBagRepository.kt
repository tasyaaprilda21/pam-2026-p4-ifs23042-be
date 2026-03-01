package org.delcom.repositories

import org.delcom.entities.Bag

interface IBagRepository {
    suspend fun getBags(search: String): List<Bag>
    suspend fun getBagById(id: String): Bag?
    suspend fun getBagByName(name: String): Bag?
    suspend fun addBag(bag: Bag): String
    suspend fun updateBag(id: String, newBag: Bag): Boolean
    suspend fun removeBag(id: String): Boolean
}