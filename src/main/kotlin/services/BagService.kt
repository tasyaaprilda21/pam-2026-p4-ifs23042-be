package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.BagRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IBagRepository
import java.io.File
import java.util.UUID

class BagService(private val bagRepository: IBagRepository) {

    // Mengambil semua data tas
    suspend fun getAllBags(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val bags = bagRepository.getBags(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar tas",
            mapOf(Pair("bags", bags))
        )
        call.respond(response)
    }

    // Mengambil data tas berdasarkan id
    suspend fun getBagById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tas tidak boleh kosong!")

        val bag = bagRepository.getBagById(id)
            ?: throw AppException(404, "Data tas tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data tas",
            mapOf(Pair("bag", bag))
        )
        call.respond(response)
    }

    // Ambil data request dari multipart
    private suspend fun getBagRequest(call: ApplicationCall): BagRequest {
        val bagReq = BagRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> bagReq.nama = part.value.trim()
                        "merek" -> bagReq.merek = part.value.trim()
                        "harga" -> bagReq.harga = part.value.toDoubleOrNull() ?: 0.0
                        "warna" -> bagReq.warna = part.value.trim()
                        "bahan" -> bagReq.bahan = part.value.trim()
                        "stok" -> bagReq.stok = part.value.toIntOrNull() ?: 0
                        "deskripsi" -> bagReq.deskripsi = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/bags/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    bagReq.pathGambar = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return bagReq
    }

    // Validasi request
    private fun validateBagRequest(bagReq: BagRequest) {
        val validatorHelper = ValidatorHelper(bagReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("merek", "Merek tidak boleh kosong")
        validatorHelper.required("warna", "Warna tidak boleh kosong")
        validatorHelper.required("bahan", "Bahan tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(bagReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar tas gagal diupload!")
        }
    }

    // Menambahkan data tas
    suspend fun createBag(call: ApplicationCall) {
        val bagReq = getBagRequest(call)

        validateBagRequest(bagReq)

        val existBag = bagRepository.getBagByName(bagReq.nama)
        if (existBag != null) {
            val tmpFile = File(bagReq.pathGambar)
            if (tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Tas dengan nama ini sudah terdaftar!")
        }

        val bagId = bagRepository.addBag(bagReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data tas",
            mapOf(Pair("bagId", bagId))
        )
        call.respond(response)
    }

    // Mengubah data tas
    suspend fun updateBag(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tas tidak boleh kosong!")

        val oldBag = bagRepository.getBagById(id)
            ?: throw AppException(404, "Data tas tidak tersedia!")

        val bagReq = getBagRequest(call)

        if (bagReq.pathGambar.isEmpty()) {
            bagReq.pathGambar = oldBag.pathGambar
        }

        validateBagRequest(bagReq)

        if (bagReq.nama != oldBag.nama) {
            val existBag = bagRepository.getBagByName(bagReq.nama)
            if (existBag != null) {
                val tmpFile = File(bagReq.pathGambar)
                if (tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Tas dengan nama ini sudah terdaftar!")
            }
        }

        if (bagReq.pathGambar != oldBag.pathGambar) {
            val oldFile = File(oldBag.pathGambar)
            if (oldFile.exists()) oldFile.delete()
        }

        val isUpdated = bagRepository.updateBag(id, bagReq.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data tas!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data tas",
            null
        )
        call.respond(response)
    }

    // Menghapus data tas
    suspend fun deleteBag(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tas tidak boleh kosong!")

        val oldBag = bagRepository.getBagById(id)
            ?: throw AppException(404, "Data tas tidak tersedia!")

        val oldFile = File(oldBag.pathGambar)

        val isDeleted = bagRepository.removeBag(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data tas!")
        }

        if (oldFile.exists()) oldFile.delete()

        val response = DataResponse(
            "success",
            "Berhasil menghapus data tas",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar tas
    suspend fun getBagImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val bag = bagRepository.getBagById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(bag.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}