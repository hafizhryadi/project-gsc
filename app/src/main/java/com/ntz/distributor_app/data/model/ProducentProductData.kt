package com.ntz.distributor_app.data.model

import androidx.annotation.Keep

@Keep
data class ProducentProductData(
    var id: String = "",
    var stuffName : String = "",
    var category : String = "",
    var price : Double = 0.0,
    var description : String = "",
    var photo : String = ""
)
