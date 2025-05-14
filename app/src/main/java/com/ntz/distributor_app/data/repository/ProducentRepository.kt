package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.data.model.ProducentProductData
import com.ntz.distributor_app.firebasedatabase.FirebaseRealtimeProducent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProducentRepository @Inject constructor(
    private val firebaseRealtimeProducent: FirebaseRealtimeProducent
) {
    fun addProduct(
        id: String,
        fullname: String,
        nickname: String,
        email: String,
        phoneNumber: String,
        country: String,
        gender: String,
        address: String,
        city: String,
        regency: String,
        product: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeProducent.setProducentData(
        id,
        fullname,
        nickname,
        email,
        phoneNumber,
        country,
        gender,
        address,
        city,
        regency,
        product,
        onSuccess,
        onFailure
    )

    fun addProduct(
        id: String,
        stuffName : String,
        category : String,
        price : String,
        description : String,
        photo : String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeProducent.setProduct(
        id,
        stuffName,
        category,
        price,
        description,
        photo,
        onSuccess,
        onFailure
    )

    fun getProduct(
        producentId : String,
        onSuccess: (List<ProducentProductData>) -> List<ProducentProductData>,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeProducent.getProduct(
        producentId,
        onSuccess,
        onFailure
    )
}