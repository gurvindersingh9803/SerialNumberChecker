package com.example.serialnumberchecker

sealed class SerialNumberCase(val serialNumber: String? = null, val errorMsg: String? = "something went wrong...") {

    class default : SerialNumberCase()

    class isLoading : SerialNumberCase()

    class onSuccess(serialNumber: String) : SerialNumberCase(serialNumber)

    class onError(errorMsg: String) : SerialNumberCase(errorMsg)
}