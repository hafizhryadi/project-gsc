package com.ntz.distributor_app.firebasedatabase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.ntz.distributor_app.data.model.AgentData

/**
 * A class that acts as an agent for interacting with Firebase Realtime Database,
 * specifically for managing agent-related data.
 * this class contain function :
 * - firebaseInitAgent() : DatabaseReference
 * - getUidandEmailUser(): Pair<String, String
 * - setAgentData()
 * - getAgentData()
 * - updateAgentData()
 */
class FirebaseRealtimeAgent {


    fun firebaseInitAgent() : DatabaseReference{
        return Firebase.database.reference
    }

    fun getUidandEmailUser(): Pair<String, String> {
        val user = Firebase.auth.currentUser
        val uid = user?.uid ?: ""
        val email = user?.email ?: ""
        return Pair(uid, email)
    }

    fun setAgentData(
        userId: String = getUidandEmailUser().first,
        fullname: String = "",
        nickname: String = "",
        email: String = getUidandEmailUser().second,
        phoneNumber: String = "",
        country: String = "",
        gender: String = "",
        address: String = "",
        city: String = "",
        regency: String = "",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit)
    {
        val dataMap = mapOf(
            AgentData::id.name to userId,
            AgentData::fullname.name to fullname,
            AgentData::nickname.name to nickname,
            AgentData::email.name to email,
            AgentData::phoneNumber.name to phoneNumber,
            AgentData::country.name to country,
            AgentData::gender.name to gender,
            AgentData::address.name to address,
            AgentData::city.name to city,
            AgentData::regency.name to regency
        )

        // first, update role, and then update data
        firebaseInitAgent().child("users").child(userId).child("role").setValue("agen")
            .addOnSuccessListener {
                firebaseInitAgent().child("agents").child(userId).setValue(dataMap)
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

    fun getAgentData(
        userId: String = getUidandEmailUser().first,
        onSuccess: (List<AgentData>) -> List<AgentData>,
        onFailure: (Exception) -> Unit
    ){
        firebaseInitAgent().child("agents").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userDataList : MutableList<AgentData> = mutableListOf()
                snapshot.children.forEach {
                    val userData = it.getValue(AgentData::class.java)
                    userData?.let {
                        userDataList.add(it)
                    }
                }
                onSuccess(userDataList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }

        })
    }

    fun updateAgentData(
        userId: String = getUidandEmailUser().first,
        fullname: String? = null,
        nickname: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        country: String? = null,
        gender: String? = null,
        address: String? = null,
        city: String? = null,
        regency: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>()
        fullname?.let { updates[AgentData::fullname.name] = it }
        nickname?.let { updates[AgentData::nickname.name] = it }
        email?.let { updates[AgentData::email.name] = it }
        phoneNumber?.let { updates[AgentData::phoneNumber.name] = it }
        country?.let { updates[AgentData::country.name] = it }
        gender?.let { updates[AgentData::gender.name] = it }
        address?.let { updates[AgentData::address.name] = it }
        city?.let { updates[AgentData::city.name] = it }
        regency?.let { updates[AgentData::regency.name] = it }

        firebaseInitAgent().child("agents").child(userId).updateChildren(updates)
            .addOnSuccessListener {
                firebaseInitAgent().child("users").child(userId).updateChildren(updates)
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


}