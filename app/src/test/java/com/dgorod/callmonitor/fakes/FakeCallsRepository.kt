package com.dgorod.callmonitor.fakes

import com.dgorod.callmonitor.data.repository.CallsRepository
import com.dgorod.callmonitor.data.service.CallStatus
import com.dgorod.callmonitor.domain.model.Call

class FakeCallsRepository: CallsRepository {
    var isOnCall = false
    val calls = mutableListOf<Call>()

    override suspend fun getOngoingCall(): CallStatus = CallStatus(ongoing = isOnCall)

    override suspend fun getCalls(limit: Int): List<Call> = calls
}