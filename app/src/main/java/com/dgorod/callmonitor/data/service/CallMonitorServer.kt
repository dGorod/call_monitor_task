package com.dgorod.callmonitor.data.service

import com.dgorod.callmonitor.data.CallsQueryStorage
import com.dgorod.callmonitor.data.repository.CallsRepository
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calls monitoring server based on embedded Ktor server.
 * API:
 *  /       - server status
 *  /status - current device call status
 *  /log    - log of previous calls
 *
 * @param callsRepository calls information provider.
 */
class CallMonitorServer(
    private val callsRepository: CallsRepository,
    private val callsQueryStorage: CallsQueryStorage
) {

    companion object {
        private const val ROUTE_STATUS = "status"
        private const val ROUTE_LOG = "log"
        private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK)
    }

    /**
     * Returns server instance to be used. Disposed and can't be reused after stop.
     *
     * @param ipAddress
     * @param port
     * @return server instance.
     */
    fun getInstance(ipAddress: String, port: Int): ApplicationEngine =
        embeddedServer(Netty, port = port) {
            val host = "$ipAddress:$port"
            val services = arrayListOf(
                ServerRoute(ROUTE_STATUS, "$host/$ROUTE_STATUS"),
                ServerRoute(ROUTE_LOG, "$host/$ROUTE_LOG")
            )
            var startTime = 0L

            install(ContentNegotiation) {
                gson { setPrettyPrinting() }
            }

            environment.monitor.subscribe(ApplicationStarted) {
                startTime = System.currentTimeMillis()
            }

            routing {
                get("/") {
                    call.respond(ServerMetadata(formatter.format(Date(startTime)), services))
                }

                get(ROUTE_STATUS) {
                    call.respond(callsRepository.getOngoingCall())
                }

                get(ROUTE_LOG) {
                    val calls = callsRepository.getCalls().map {
                        CallLog(
                            formatter.format(it.date),
                            it.duration,
                            it.number,
                            it.name,
                            callsQueryStorage.getQueryCount(it.date)
                        )
                    }
                    call.respond(calls)
                }
            }
        }
}