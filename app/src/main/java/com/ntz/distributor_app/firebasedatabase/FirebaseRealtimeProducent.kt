package com.ntz.distributor_app.firebasedatabase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.ntz.distributor_app.data.model.ProducenData
import com.ntz.distributor_app.data.model.ProducentProductData
import java.util.UUID

class FirebaseRealtimeProducent {

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
        product: Map<String, Any> = emptyMap(),
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
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

        firebaseInitProducent().child("users").child(id).child("role").setValue("producent")
            .addOnSuccessListener {
                firebaseInitProducent().child("produsens").child(id).setValue(dataMap)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun setProduct(
        id: String = "",
        stuffName : String = "",
        category : String = "",
        price : String = "",
        description : String = "",
        photo : String = "",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
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
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getProduct(producentId : String, onSuccess: (List<ProducentProductData>) -> List<ProducentProductData>, onFailure: (Exception) -> Unit){
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
                    onSuccess(productList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.toException())
                }

            })
    }


}