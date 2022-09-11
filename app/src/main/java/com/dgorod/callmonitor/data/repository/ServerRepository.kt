package com.dgorod.callmonitor.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dgorod.callmonitor.data.service.CallMonitorServer
import io.ktor.server.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.NetworkInterface

/**
 * Repository managing app calls monitoring server.
 */
interface ServerRepository {
    fun start()
    fun stop()
    fun isRunning(): LiveData<Boolean>
    fun getHost(): String
}

/**
 * Default [ServerRepository] implementation using [CallMonitorServer].
 */
class ServerRepositoryImpl(private val server: CallMonitorServer): ServerRepository {

    companion object {
        private const val DEFAULT_PORT = 11000
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val isRunning = MutableLiveData(false)
    private val ipAddress: String by lazy { findLocalAddress() }

    private lateinit var serverInstance: ApplicationEngine

    /**
     * Asynchronously starts [CallMonitorServer] instance on the local host.
     * In case of unavailable host ignores the command.
     */
    override fun start() {
        if (isRunning.value == false && ipAddress.isNotEmpty()) {
            scope.launch {
                isRunning.postValue(true)
                serverInstance = server.getInstance(ipAddress, DEFAULT_PORT)
                serverInstance.start(wait = true)
            }
        }
    }

    /**
     * Asynchronously stops the server.
     */
    override fun stop() {
        if (isRunning.value == true) {
            scope.launch {
                serverInstance.stop()
                isRunning.postValue(false)
            }
        }
    }

    override fun isRunning(): LiveData<Boolean> = isRunning

    /**
     * @return server host in <ip address>:<port> format. (ex. 192.168.1.2:5001)
     */
    override fun getHost(): String = "$ipAddress:$DEFAULT_PORT"

    /**
     * @return available local device ip address. Otherwise empty string.
     */
    private fun findLocalAddress(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces().asSequence()
        val localAddresses = networkInterfaces.flatMap { networkInterface ->
            networkInterface.inetAddresses.asSequence()
                .filter { address -> address.isSiteLocalAddress && !address.isLoopbackAddress }
                .map { address -> address.hostAddress }
        }
        return localAddresses.firstOrNull() ?: ""
    }
}