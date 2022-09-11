package com.dgorod.callmonitor.ui.model

import android.telephony.PhoneNumberUtils.formatNumber
import com.dgorod.callmonitor.domain.model.Call
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * UI prepared call log model.
 */
data class CallUiModel(val name: String, val duration: String) {

    companion object {
        private fun getBestName(data: Call): String {
            return data.name ?: formatNumber(data.number, Locale.getDefault().country)
        }

        private fun Long.format(): String {
            val minutes = TimeUnit.SECONDS.toMinutes(this)
            val seconds = TimeUnit.SECONDS.toSeconds(this) - (minutes * 60)
            return String.format("%dm %ds", minutes, seconds)
        }
    }

    /**
     * Constructs call log model.
     * In case of absent name gets formatted phone number.
     * Formats duration into 'Nm Ns' format. (ex. 5m 25s)
     */
    //TODO: for the simplicity scope of test task formatting contains only minutes and seconds
    constructor(data: Call) : this(getBestName(data), data.duration.format())
}
