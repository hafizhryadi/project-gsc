package com.ntz.distributor_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.ntz.distributor_app.data.model.ProducenData
import com.ntz.distributor_app.data.model.ProducentProductData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

sealed class ProducentState{
    object Idle : ProducentState()
    object Loading : ProducentState()
    data class Success(val user: ProducenData) : ProducentState()
    data class Error(val message: String) : ProducentState()
}

class FirebaseRealtimeProducent : ViewModel() {

    var _producentState = MutableStateFlow<ProducentState>(ProducentState.Idle)
    val producentState : StateFlow<ProducentState> = _producentState

    val productListData = MutableStateFlow<List<ProducentProductData>>(emptyList())


    fun firebaseInitProducent() : DatabaseReference {
        return Firebase.database.reference
    }

    fun getUidandEmailUser(): Pair<String, String> {
        val user = Firebase.auth.currentUser
        val uid = user?.uid ?: ""
        val email = user?.email ?: ""
        return Pair(uid, email)
    }

    fun setProducentData(
        id: String = getUidandEmailUser().first,
        fullname: String = "",
        nickname: String = "",
        email: String = getUidandEmailUser().second,
        phoneNumber: String = "",
        country: String = "",
        gender: String = "",
        address: String = "",
        city: String = "",
        regency: String = "",
        product: Map<String, Any> = emptyMap()
    ){
        val dataMap = mapOf(
            ProducenData::id.name to id,
            ProducenData::fullname.name to fullname,
            ProducenData::nickname.name to nickname,
            ProducenData::email.name to email,
            ProducenData::phoneNumber.name to phoneNumber,
            ProducenData::country.name to country,
            ProducenData::gender.name to gender,
            ProducenData::address.name to address,
            ProducenData::city.name to city,
            ProducenData::regency.name to regency,
            ProducenData::product.name to product
        )

        firebaseInitProducent().child("produsens").child(id).setValue(dataMap)
            .addOnSuccessListener {
                Log.d("FirebaseRealtimeProducent", "Producent data set successfully")
            }
            .addOnFailureListener {
                _producentState.value = ProducentState.Error("Error setting producent data: ${it.message}")
                Log.e("FirebaseRealtimeProducent", "Error setting producent data: ${it.message}")
            }
    }

    fun setProduct(
        id: String = getUidandEmailUser().first,
        stuffName : String = "",
        category : String = "",
        price : String = "",
        description : String = "",
        photo : String = ""
    ){
        val productRef = firebaseInitProducent().child("produsens").child(id).child("products").push()
        val productId = productRef.key ?: UUID.randomUUID().toString().replace("-", "")

        val productData = mapOf(
            ProducentProductData::id.name to productId,
            ProducentProductData::stuffName.name to stuffName,
            ProducentProductData::category.name to category,
            ProducentProductData::price.name to price,
            ProducentProductData::description.name to description,
            ProducentProductData::photo.name to photo
        )

        productRef.setValue(productData)
            .addOnSuccessListener {
                Log.d("FirebaseRealtimeProducent", "Product data set successfully")
            }
            .addOnFailureListener {
                _producentState.value = ProducentState.Error("Error setting product data: ${it.message}")
                Log.e("FirebaseRealtimeProducent", "Error setting product data: ${it.message}")
            }
    }

    fun getProduct(producentId : String){
        firebaseInitProducent().child("produsens").child(producentId).child("products")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList : MutableList<ProducentProductData> = mutableListOf()
                    snapshot.children.forEach {
                        val productData = it.getValue(ProducentProductData::class.java)
                        productData?.let {
                            productList.add(it)
                        }
                    }
                    productListData.value = productList
                    Log.d("FirebaseRealtimeProducent", "ProductData: $productList")
                }

                override fun onCancelled(error: DatabaseError) {
                    _producentState.value = ProducentState.Error("Error getting product data: ${error.message}")
                }

            })
    }


}