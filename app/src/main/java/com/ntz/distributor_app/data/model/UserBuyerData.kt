package com.ntz.distributor_app.data.model

// keep
data class UserBuyerData(
    var id : String = "",
    var buyerName : String = "",
    var locationBuyer : String = "",
    var buyerAddress : String = "",
    var phoneNumberBuyer : String = "",
    var categoryBuyer : List<String> = emptyList(),
)
