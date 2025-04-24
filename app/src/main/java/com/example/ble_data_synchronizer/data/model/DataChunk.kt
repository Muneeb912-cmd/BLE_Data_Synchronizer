package com.example.ble_data_synchronizer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "data_chunks")
data class DataChunk(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val data: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUploaded: Boolean = false
)
