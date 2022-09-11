package com.dgorod.callmonitor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgorod.callmonitor.domain.CallsInteractor
import com.dgorod.callmonitor.domain.ServerInteractor
import com.dgorod.callmonitor.ui.model.CallUiModel
import kotlinx.coroutines.launch

/**
 * ViewModel to handle [com.dgorod.callmonitor.ui.MainActivity] functionality.
 */
class MainActivityViewModel(
    private val callsInteractor: CallsInteractor,
    private val serverInteractor: ServerInteractor
): ViewModel() {

    private val _callsList = MutableLiveData<List<CallUiModel>>()
    val callsList = _callsList

    val isServerRunning: LiveData<Boolean> = serverInteractor.getServerStatus()

    /**
     * Request calls log. Operation result update posted in [callsList].
     */
    fun fetchCallsLog() {
        viewModelScope.launch {
            _callsList.value = callsInteractor.getCallsList()
        }
    }

    /**
     * @return server host address.
     */
    fun getServerHost():String = serverInteractor.getServerHost()

    /**
     * Toggles calls monitoring server status (on/off).
     */
    fun toggleServerStatus() {
        serverInteractor.toggleServerStatus()
    }
}