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
import com.ntz.distributor_app.data.model.AgentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

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

sealed class AgentState{
    object Idle : AgentState()
    object Loading : AgentState()
    data class Success(val user: AgentData) : AgentState()
    data class Error(val message: String) : AgentState()
}

class FirebaseRealtimeAgent : ViewModel(){

    var _agentState = MutableStateFlow<AgentState>(AgentState.Idle)
    val agentState : StateFlow<AgentState> = _agentState

    var userData : MutableStateFlow<MutableList<AgentData>> = MutableStateFlow(mutableListOf())

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
        regency: String = ""
    ){
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
                        _agentState.value = AgentState.Success(dataMap as AgentData)
                        Log.d("FirebaseRealtimeAgent", "Agent data set successfully")
                    }
                    .addOnFailureListener {
                        _agentState.value = AgentState.Error("Error setting agent data: ${it.message}")
                        Log.e("FirebaseRealtimeAgent", "Error setting agent data: ${it.message}")
                    }
            }
            .addOnFailureListener {
                _agentState.value = AgentState.Error("Error setting user role: ${it.message}")
                Log.e("FirebaseRealtimeAgent", "Error setting user role: ${it.message}")
            }
    }

    fun getAgentData() {
        firebaseInitAgent().child("agents").child(getUidandEmailUser().first).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userDataList : MutableList<AgentData> = mutableListOf()
                snapshot.children.forEach {
                    val userData = it.getValue(AgentData::class.java)
                    userData?.let {
                        userDataList.add(it)
                    }
                }
                userData.value = userDataList
                Log.d("FirebaseRealtimeAgent", "AgentData: $userDataList")
            }

            override fun onCancelled(error: DatabaseError) {
                _agentState.value = AgentState.Error("Error getting agent data: ${error.message}")
                Log.e("FirebaseRealtimeAgent", "Error getting agent data: ${error.message}")
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
        regency: String? = null
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
                        _agentState.value = AgentState.Success(updates as AgentData)
                        Log.d("FirebaseRealtimeAgent", "Agent data updated successfully")
                    }
                    .addOnFailureListener {
                        _agentState.value = AgentState.Error("Error updating agent data: ${it.message}")
                        Log.e("FirebaseRealtimeAgent", "Error updating agent data: ${it.message}")
                    }
            }
            .addOnFailureListener {
                _agentState.value = AgentState.Error("Error updating user role: ${it.message}")
                Log.e("FirebaseRealtimeAgent", "Error updating user role: ${it.message}")
            }
    }


}