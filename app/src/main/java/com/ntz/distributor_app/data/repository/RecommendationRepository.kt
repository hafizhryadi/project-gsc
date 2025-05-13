package com.ntz.distributor_app.data.repository

import android.util.Log
import com.ntz.distributor_app.data.model.UserDistributionData
import com.ntz.distributor_app.data.model.UserPreferences
import com.ntz.distributor_app.data.remote.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val geminiApiService: GeminiApiService
) {
    suspend fun getRecommendations(
        preferences: UserPreferences,
        availableUserDistributionData: List<UserDistributionData>
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        val prompt = buildRecommendationPrompt(preferences, availableUserDistributionData)

        try {
            val response = geminiApiService.generateRecommendation(prompt)
            val responseText = response?.text

            if (responseText.isNullOrBlank()) {
                Log.e("RecommendationRepository", "Response text is null or blank.")
                return@withContext Result.failure(Exception("Response text is null or blank."))
            }

            Log.d("RecommendationRepository", "Response text: $responseText")
            val recommendationNames = responseText
                .split(",")
                .map { it.trim().removePrefix("-").trim() }
                .filter { it.isNotBlank() }

            Log.i("RecommendationRepository", "Recommendations: $recommendationNames")

            val validNames = recommendationNames.filter { recName ->
                availableUserDistributionData.any { dist -> dist.name.equals(recName, ignoreCase = true)}
            }

            if (validNames.isEmpty() && recommendationNames.isNotEmpty()) {
                Log.w("RecommendationRepository", "No valid recommendations found, but some names were returned.")
            } else if (validNames.isEmpty()) {
                Log.e("RecommendationRepository", "No valid recommendations found.")
                return@withContext Result.failure(Exception("No valid recommendations found."))
            }

            return@withContext Result.success(validNames.ifEmpty { recommendationNames })
        } catch (e: Exception) {
            Log.e("RecommendationRepository", "Error generating recommendations: ${e.message}")
            return@withContext Result.failure(e)
        }
    }

    private fun buildRecommendationPrompt(
        prefs: UserPreferences,
        userDistributionData: List<UserDistributionData>
    ): String {
        val distributorListString = userDistributionData.joinToString("\n") {
            " - Nama: ${it.name}, Lokasi: ${it.location}, Kategori: ${it.productCategories.joinToString(", ")}, Detail: ${it.details}"
        }

        val prefCategoriesString = if (prefs.preferredCategories.isNotEmpty()) {
            prefs.preferredCategories.joinToString(", ")
        } else {
            "Tidak ada kategori yang dipilih"
        }
        val prefLocationString = if (prefs.preferredLocation.isNotBlank()) prefs.preferredLocation else "Tidak ada lokasi yang dipilih"
        val prefMinOrderString = prefs.minOrder?.let { "Minimal order: $it" } ?: "Tidak ada minimal order yang dipilih"

        return """
            Anda adalah sistem pakar rekomendasi distributor di Indonesia.
            Tugas Anda adalah merekomendasikan distributor yang paling cocok berdasarkan preferensi pengguna dari daftar yang tersedia.

            Preferensi Pengguna:
            * Kategori Produk yang diminati: $prefCategoriesString
            * Preferensi Lokasi: $prefLocationString
            * Minimal Order yang diinginkan: $prefMinOrderString

            Daftar Distributor Tersedia:
            $distributorListString

            Instruksi:
            1.  Analisis preferensi pengguna dan bandingkan dengan detail setiap distributor.
            2.  Pilih maksimal 3 (tiga) distributor yang paling relevan dan cocok dengan preferensi pengguna.
            3.  Prioritaskan kecocokan pada kategori produk dan lokasi. Pertimbangkan minimal order jika pengguna menyatakannya.
            4.  **Format Output:** Kembalikan HANYA nama-nama distributor yang direkomendasikan, dipisahkan oleh koma (,). Jangan sertakan penjelasan, nomor, atau teks tambahan lainnya.
                Contoh Output Valid: PT Sinar Jaya Abadi, UD Sumber Rejeki, Agen Sejahtera
                Contoh Output TIDAK Valid: 1. PT Sinar Jaya Abadi karena cocok...

            Berikan rekomendasi Anda sekarang:
        """.trimIndent()
    }
}