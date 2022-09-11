package com.dgorod.callmonitor.data.repository

import android.Manifest.permission.READ_PHONE_STATE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.CallLog.Calls
import android.provider.ContactsContract.PhoneLookup
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat.checkSelfPermission
import com.dgorod.callmonitor.data.service.CallStatus
import com.dgorod.callmonitor.domain.model.Call

/**
 * Repository managing calls: log, status.
 */
interface CallsRepository {
    suspend fun getOngoingCall(): CallStatus
    suspend fun getCalls(limit:Int  = 100): List<Call>
}

/**
 * Default [CallsRepository] implementation based on Android Content Provider.
 */
class CallsRepositoryImpl(private val ctx: Context): CallsRepository {

    private val telecomService = ctx.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    /**
     * @return ongoing call status.
     */
    override suspend fun getOngoingCall(): CallStatus {
        val havePermission = checkSelfPermission(ctx, READ_PHONE_STATE) == PERMISSION_GRANTED
        return if (havePermission && telecomService.isInCall) {
            CallStatus(ongoing = true)
        } else {
            CallStatus(ongoing = false)
        }
    }

    /**
     * Retrieve calls log. Declined incoming calls are ignored.
     *
     * @param limit amount of logs to retrieve.
     * @return list of calls. Empty list returned in case of unavailable Content Provider data.
     */
    override suspend fun getCalls(limit: Int): List<Call> {
        val calls = mutableListOf<Call>()

        val uri = Calls.CONTENT_URI
        val columns = arrayOf(Calls.CACHED_NAME, Calls.NUMBER, Calls.DATE, Calls.DURATION)
        val selection = "${Calls.DURATION} > 0"
        val sortOrder = Calls.DATE + " DESC"
        val cursor = ctx.contentResolver.query(
            uri, columns, selection, null, sortOrder
        ) ?: return emptyList()

        val numberIndex = cursor.getColumnIndex(Calls.NUMBER)
        val nameIndex = cursor.getColumnIndex(Calls.CACHED_NAME)
        val dateIndex = cursor.getColumnIndex(Calls.DATE)
        val durationIndex = cursor.getColumnIndex(Calls.DURATION)

        while (cursor.moveToNext() && cursor.position < limit) {
            val number = cursor.getString(numberIndex)
            val name = cursor.getString(nameIndex) ?: getContactName(number)
            val date = cursor.getLong(dateIndex)
            val duration = cursor.getLong(durationIndex)
            val call = Call(name, number, date, duration)
            calls.add(call)
        }

        cursor.close()
        return calls
    }

    /**
     * Retrieve name for a number from contacts.
     *
     * @param number phone number.
     * @return contact name, otherwise null.
     */
    private fun getContactName(number: String): String? {
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val columns = arrayOf(PhoneLookup.DISPLAY_NAME)
        val cursor = ctx.contentResolver.query(
            uri, columns, null, null, null
        ) ?: return null // note return if null

        val nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME)

        return if (cursor.moveToFirst()) {
            cursor.getString(nameIndex).also { cursor.close() }
        } else {
            null
        }
    }
}