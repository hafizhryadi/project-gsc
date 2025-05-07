package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.data.model.Distributor
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistributorRepository @Inject constructor() {
    private val dummyDistributors = listOf(
        Distributor("D001", "PT Sinar Jaya Abadi", "Jakarta Pusat", listOf("Makanan", "Minuman Ringan", "ATK"), "Pengiriman cepat, min order Rp 500.000, Terima COD", "https://via.placeholder.com/150/FF0000/FFFFFF?text=SJA"),
        Distributor("D002", "CV Maju Bersama", "Surabaya", listOf("Elektronik", "Gadget"), "Garansi resmi 1 tahun, cicilan 0%", "https://via.placeholder.com/150/00FF00/FFFFFF?text=MB"),
        Distributor("D003", "UD Sumber Rejeki", "Bandung", listOf("Makanan Ringan", "Kebutuhan Pokok"), "Fokus produk UKM lokal, min order Rp 100.000", "https://via.placeholder.com/150/0000FF/FFFFFF?text=SR"),
        Distributor("D004", "Toko Grosir Makmur", "Medan", listOf("Minuman Ringan", "Kebutuhan Pokok", "Pembersih"), "Harga bersaing, buka 24 jam"),
        Distributor("D005", "Agen Sejahtera", "Jakarta Selatan", listOf("ATK", "Peralatan Kantor"), "Layanan antar khusus perkantoran", "https://via.placeholder.com/150/FFFF00/000000?text=AS")

    )
    suspend fun getAllDistributors(): List<Distributor> {
        delay(1200)
        return dummyDistributors
    }

    suspend fun getDistributorById(id: String): Distributor? {
        delay(300)
        return dummyDistributors.find { it.id == id }
    }
}