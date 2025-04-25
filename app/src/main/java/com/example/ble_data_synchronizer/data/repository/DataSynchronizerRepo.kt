package com.example.ble_data_synchronizer.data.repository

import com.example.ble_data_synchronizer.data.model.DataChunk
import com.example.ble_data_synchronizer.data.datasource.local.DataChunkDao
import com.example.ble_data_synchronizer.data.datasource.remote.FirestoreDataSource
import com.example.ble_data_synchronizer.utils.ConnectionStatus
import com.example.ble_data_synchronizer.utils.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSynchronizerRepository @Inject constructor(
    private val dataChunkDao: DataChunkDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val connectivityObserver: ConnectivityObserver,
    private val appScope: CoroutineScope
) {
    // Monitor connectivity status
    val connectionStatus: StateFlow<ConnectionStatus> = connectivityObserver.observe()
        .stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = ConnectionStatus.UNAVAILABLE
        )

    // Get all stored data chunks
    val allDataChunks: Flow<List<DataChunk>> = dataChunkDao.getAllDataChunks()

    // Get pending uploads count
    val pendingUploadsCount: Flow<Int> = dataChunkDao.getNotUploadedChunks()
        .map { it.size }

    init {
        // Start monitoring connection status
        appScope.launch {
            connectionStatus.collect { status ->
                if (status == ConnectionStatus.AVAILABLE) {
                    synchronizePendingData()
                }
            }
        }
    }

    // Store a data chunk locally
    suspend fun storeDataChunk(dataChunk: DataChunk) {
        withContext(Dispatchers.IO) {
            dataChunkDao.insertDataChunk(dataChunk)

            // If connected, attempt to upload immediately
            if (connectionStatus.value == ConnectionStatus.AVAILABLE) {
                uploadDataChunk(dataChunk)
            }
        }
    }

    // Upload a single data chunk
    private suspend fun uploadDataChunk(dataChunk: DataChunk) {
        withContext(Dispatchers.IO) {
            firestoreDataSource.uploadDataChunk(dataChunk)
                .onSuccess {
                    // Mark as uploaded in local DB
                    dataChunkDao.markAsUploaded(dataChunk.id)
                }
        }
    }

    // Synchronize all pending data
    suspend fun synchronizePendingData() {
        withContext(Dispatchers.IO) {
            val pendingChunks = dataChunkDao.getNotUploadedChunks().map { it }
                .firstOrNull() ?: emptyList()

            pendingChunks.forEach { chunk ->
                uploadDataChunk(chunk)
            }
        }
    }

    // Generate mock data chunk (simulating BLE data)
    fun generateDataChunk(data: String): DataChunk {
        return DataChunk(
            data = data,
            timestamp = System.currentTimeMillis()
        )
    }
}