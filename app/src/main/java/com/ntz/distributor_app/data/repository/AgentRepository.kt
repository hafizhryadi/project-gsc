package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.data.model.AgentData
import com.ntz.distributor_app.firebasedatabase.FirebaseRealtimeAgent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepository @Inject constructor(
    private val firebaseRealtimeAgent: FirebaseRealtimeAgent
) {
    fun addAgent(
        userId : String,
        fullname : String,
        nickname : String,
        email : String,
        phoneNumber : String,
        country : String,
        gender : String,
        address : String,
        city : String,
        regency : String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeAgent.setAgentData(
        userId,
        fullname,
        nickname,
        email,
        phoneNumber,
        country,
        gender,
        address,
        city,
        regency,
        onSuccess,
        onFailure
    )

    fun getAgent(
        userId : String,
        onSuccess: (List<AgentData>) -> List<AgentData>,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeAgent.getAgentData(
        userId,
        onSuccess,
        onFailure
    )

    fun updateAgent(
        userId : String,
        fullname : String,
        nickname : String,
        email : String,
        phoneNumber : String,
        country : String,
        gender : String,
        address : String,
        city : String,
        regency : String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeAgent.updateAgentData(
        userId,
        fullname,
        nickname,
        email,
        phoneNumber,
        country,
        gender,
        address,
        city,
        regency,
        onSuccess,
        onFailure
    )
}