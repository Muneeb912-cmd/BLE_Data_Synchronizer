package com.example.ble_data_synchronizer.domain.usecases

import com.example.ble_data_synchronizer.data.model.DataChunk
import com.example.ble_data_synchronizer.data.repository.DataSynchronizerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class GenerateDataChunksUseCase @Inject constructor(
    private val repository: DataSynchronizerRepository
) {
    // Simulate data generation at specific intervals
    fun generateDataChunks(intervalMs: Long): Flow<DataChunk> = flow {
        while (true) {
            val dataChunk = repository.generateDataChunk(
                "Random Data ${UUID.randomUUID().toString().substring(0, 8)} at ${System.currentTimeMillis()}"
            )
            emit(dataChunk)
            delay(intervalMs)
        }
    }
}
