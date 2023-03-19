package com.example.bluetoothchatapp.domain.chat

sealed interface ConnectionResult {

    object ConnectionEstablished : ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage) : ConnectionResult
    data class Error(val message: String) : ConnectionResult
}
