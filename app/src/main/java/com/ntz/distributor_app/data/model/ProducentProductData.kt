package com.ntz.distributor_app.data.model

import androidx.annotation.Keep

@Keep
data class ProducentProductData(
    var id: String = "",
    var stuffName : String = "",
    var category : String = "",
    var price : String = "",
    var description : String = "",
    var photo : String = ""
)
