package com.example.ble_data_synchronizer.ui.screens.datasimulator

import com.example.ble_data_synchronizer.data.model.DataChunk
import com.example.ble_data_synchronizer.utils.ConnectionStatus
import kotlinx.coroutines.flow.StateFlow

data class DataSimulatorState(
    val isGenerating: Boolean = false,
    val intervalMs: Long = 5000, // Default 5 seconds
    val generatedCount: Int = 0,
    var allDataChunks: List<DataChunk> = emptyList(),
    var connectivitySate: ConnectionStatus = ConnectionStatus.UNAVAILABLE,
    var pendingUploadCount:Int=0
)