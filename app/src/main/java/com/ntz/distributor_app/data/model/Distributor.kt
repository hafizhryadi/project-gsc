package com.ntz.distributor_app.data.model

data class Distributor(
    val id: String,
    val name: String,
    val location: String,
    val productCategories: List<String>,
    val details: String,
    val imageUrl: String? = null
)