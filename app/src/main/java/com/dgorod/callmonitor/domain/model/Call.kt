package com.dgorod.callmonitor.domain.model

/**
 * Call log model.
 */
data class Call(
    val name: String?,
    val number: String,
    val date: Long, // in millis
    val duration: Long // in seconds
)

