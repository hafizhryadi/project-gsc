package com.ntz.distributor_app.data.model

import com.ntz.distributor_app.data.repository.AuthRepository
import java.util.UUID

data class AgentData(
    var id: String = "",
    var fullname: String = "",
    var nickname: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var country: String = "",
    var gender: String = "",
    var address: String = "",
    var city: String = "",
    var regency: String = ""
)
