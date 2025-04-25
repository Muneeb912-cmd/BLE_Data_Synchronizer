package com.example.ble_data_synchronizer.ui.screens.datasimulator

sealed class DataSimulatorIntent {
    data class StartSimulation(val intervalMs: Long) : DataSimulatorIntent()
    object StopSimulation : DataSimulatorIntent()
    data class SetInterval(val intervalMs: Long) : DataSimulatorIntent()
}