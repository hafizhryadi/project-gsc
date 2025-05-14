package com.ntz.distributor_app.data.model

import org.json.JSONObject
import java.util.UUID

data class ProducenData(
    var id: String = "",
    var fullname: String = "",
    var nickname: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var country: String = "",
    var gender: String = "",
    var address: String = "",
    var city: String = "",
    var regency: String = "",
    var product: Map<String, Any> = emptyMap()
)
