package com.dgorod.callmonitor.domain

import android.Manifest.permission.READ_CALL_LOG
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission
import com.dgorod.callmonitor.data.repository.CallsRepository
import com.dgorod.callmonitor.ui.model.CallUiModel

/**
 * Interactor containing use cases related to calls.
 */
class CallsInteractor(private val ctx: Context, private val callsRepository: CallsRepository) {

    /**
     * @return prepared for UI usage log of calls.
     */
    suspend fun getCallsList(): List<CallUiModel> {
        return if (checkSelfPermission(ctx, READ_CALL_LOG) == PERMISSION_GRANTED) {
            callsRepository.getCalls().map { CallUiModel(it) }
        } else {
            emptyList()
        }
    }
}