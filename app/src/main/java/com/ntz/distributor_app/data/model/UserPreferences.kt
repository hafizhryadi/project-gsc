package com.ntz.distributor_app.data.model

data class UserPreferences (
    val preferredCategories: Set<String> = emptySet(),
    val preferredLocation: String = "",
    val minOrder: Int? = null
)