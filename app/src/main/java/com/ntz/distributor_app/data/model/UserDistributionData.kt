package com.ntz.distributor_app.data.model

data class UserDistributionData(
    val id: String,
    val name: String,
    val location: String,
    val productCategories: List<String>,
    val details: String, // for buyer next add productDetails
    val imageUrl: String? = null,
    val phoneNumber : String = "",

    /*val rating: Double? = null,
    val buyerTrustPercentage: Double? = null,
    val totalBuyerCount: Int? = null*/



    // for make good, we add some more data like rating, how much buyer trust this distributor
    // how much stuff sell from this distributor
)