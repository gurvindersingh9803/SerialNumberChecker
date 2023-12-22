package com.example.serialnumberchecker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pax.poslink.CommSetting
import com.pax.poslink.ManageRequest
import com.pax.poslink.ManageResponse
import com.pax.poslink.PosLink
import com.pax.poslink.ProcessTransResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SerialNumberViewModel : ViewModel() {

    private val serialNumber = MutableStateFlow<SerialNumberCase>(SerialNumberCase.default())
    val _serialNumber: StateFlow<SerialNumberCase> get() = serialNumber

    private fun setComm(): CommSetting {
        val commSetting = CommSetting()
        commSetting.type = CommSetting.AIDL
        return commSetting
    }

    fun getPaxSerialNumber(posLink: PosLink) {
        viewModelScope.launch(Dispatchers.Default) {
            serialNumber.emit(SerialNumberCase.isLoading())
            posLink.SetCommSetting(setComm())
            val manageRequest = ManageRequest()
            manageRequest.TransType = manageRequest.ParseTransType("INIT")
            posLink.ManageRequest = manageRequest
            val processTransResult: ProcessTransResult = posLink.ProcessTrans()
            try {
                val manageResponse: ManageResponse? = posLink.ManageResponse
                if (manageResponse != null) {
                    println("Serial Number ${manageResponse.SN}") // GET THE SN OF THE TERMINAL
                    serialNumber.emit(SerialNumberCase.onSuccess(manageResponse.SN))
                } else {
                    serialNumber.emit(SerialNumberCase.onError("Error...."))
                }
            } catch (e: Exception) {
                serialNumber.emit(SerialNumberCase.onError("Retry......."))
                e.printStackTrace()
            }
        }
    }
}
