package com.dgorod.callmonitor.data.service

/**
 * Collection of data models used by [CallMonitorServer].
 */

data class ServerMetadata(val start:String, val services: List<ServerRoute>)

data class ServerRoute(val name: String, val uri: String)

data class CallStatus(val ongoing: Boolean, val number: String? = null, val name: String? = null)

data class CallLog(
    val beginning:String,
    val duration: Long,
    val number: String,
    val name: String?,
    val timesQueried: Int
)