package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.data.model.UserDistributionData
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistributorRepository @Inject constructor() {
    private val dummyUserDistributionData = listOf(
        UserDistributionData("D001", "PT Sinar Jaya Abadi", "Jakarta Pusat", listOf("Makanan", "Minuman Ringan", "ATK"), "Pengiriman cepat, min order Rp 500.000, Terima COD", "https://via.placeholder.com/150/FF0000/FFFFFF?text=SJA"),
        UserDistributionData("D002", "CV Maju Bersama", "Surabaya", listOf("Elektronik", "Gadget"), "Garansi resmi 1 tahun, cicilan 0%", "https://via.placeholder.com/150/00FF00/FFFFFF?text=MB"),
        UserDistributionData("D003", "UD Sumber Rejeki", "Bandung", listOf("Makanan Ringan", "Kebutuhan Pokok"), "Fokus produk UKM lokal, min order Rp 100.000", "https://via.placeholder.com/150/0000FF/FFFFFF?text=SR"),
        UserDistributionData("D004", "Toko Grosir Makmur", "Medan", listOf("Minuman Ringan", "Kebutuhan Pokok", "Pembersih"), "Harga bersaing, buka 24 jam"),
        UserDistributionData("D005", "Agen Sejahtera", "Jakarta Selatan", listOf("ATK", "Peralatan Kantor"), "Layanan antar khusus perkantoran", "https://via.placeholder.com/150/FFFF00/000000?text=AS")

    )
    suspend fun getAllDistributors(): List<UserDistributionData> {
        delay(1200)
        return dummyUserDistributionData
    }

    suspend fun getDistributorById(id: String): UserDistributionData? {
        delay(300)
        return dummyUserDistributionData.find { it.id == id }
    }
}