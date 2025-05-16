package com.ntz.distributor_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRecommendation(
    val id: String = "",
    val nama: String = "",
    val kota: String = "",
    val kabupaten: String = ""
)
