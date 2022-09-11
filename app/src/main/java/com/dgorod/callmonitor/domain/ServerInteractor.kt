package com.dgorod.callmonitor.domain

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.dgorod.callmonitor.data.repository.ServerRepository
import com.dgorod.callmonitor.data.service.CallMonitorService

/**
 * Interactor containing use cases related to calls monitoring server.
 */
class ServerInteractor(private val ctx: Context, private val serverRepository: ServerRepository) {

    private val serviceIntent = Intent(ctx, CallMonitorService::class.java)

    /**
     * @return server host.
     */
    fun getServerHost(): String = serverRepository.getHost()

    /**
     * @return server status.
     */
    fun getServerStatus(): LiveData<Boolean> = serverRepository.isRunning()

    /**
     * Toggles server status (on/off). Change may not happen immediately.
     */
    fun toggleServerStatus() {
        if (serverRepository.isRunning().value == true) {
            ctx.stopService(serviceIntent)
        } else {
            ctx.startService(serviceIntent)
        }
    }
}