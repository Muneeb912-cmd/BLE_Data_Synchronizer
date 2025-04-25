package com.example.ble_data_synchronizer.domain.usecases

import com.example.ble_data_synchronizer.data.model.DataChunk
import com.example.ble_data_synchronizer.data.repository.DataSynchronizerRepository
import com.example.ble_data_synchronizer.utils.ConnectionStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SynchronizeDataUseCase @Inject constructor(
    private val repository: DataSynchronizerRepository
) {
    suspend fun storeDataChunk(dataChunk: DataChunk) {
        repository.storeDataChunk(dataChunk)
    }

    suspend fun synchronizeData() {
        repository.synchronizePendingData()
    }

    fun getConnectionStatus(): Flow<ConnectionStatus> {
        return repository.connectionStatus
    }

    fun getPendingUploadsCount(): Flow<Int> {
        return repository.pendingUploadsCount
    }

    fun getAllDataChunks(): Flow<List<DataChunk>> {
        return repository.allDataChunks
    }
}