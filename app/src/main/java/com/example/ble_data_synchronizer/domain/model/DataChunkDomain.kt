package com.example.ble_data_synchronizer.domain.model

data class DataChunkDomain(
    val id: String,
    val data: String,
    val timestamp: Long,
    val isUploaded: Boolean
)