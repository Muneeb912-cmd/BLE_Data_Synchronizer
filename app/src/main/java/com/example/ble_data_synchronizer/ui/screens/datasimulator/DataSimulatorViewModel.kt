package com.example.ble_data_synchronizer.ui.screens.datasimulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble_data_synchronizer.data.model.DataChunk
import com.example.ble_data_synchronizer.data.repository.DataSynchronizerRepository
import com.example.ble_data_synchronizer.domain.usecases.GenerateDataChunksUseCase
import com.example.ble_data_synchronizer.domain.usecases.SynchronizeDataUseCase
import com.example.ble_data_synchronizer.utils.ConnectionStatus
import com.example.ble_data_synchronizer.utils.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataSimulatorViewModel @Inject constructor(
    private val repo: DataSynchronizerRepository,
    private val generateDataChunksUseCase: GenerateDataChunksUseCase,
    private val synchronizeDataUseCase: SynchronizeDataUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val allDataChunks: StateFlow<List<DataChunk>> = repo.allDataChunks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingUploadCount: StateFlow<Int> = repo.pendingUploadsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _state = MutableStateFlow(DataSimulatorState())
    val state: StateFlow<DataSimulatorState> = _state

    private val connectionStatus: Flow<ConnectionStatus> = connectivityObserver.observe()

    init {
        viewModelScope.launch {
            allDataChunks.collect { chunks ->
                _state.update { it.copy(allDataChunks = chunks) }
            }
        }

        viewModelScope.launch {
            connectionStatus.collect { status ->
                _state.update { it.copy(connectivitySate = status) }
            }
        }

        viewModelScope.launch {
            pendingUploadCount.collect { count ->
                _state.update { it.copy(pendingUploadCount = count) }
            }
        }
    }

    private var generationJob: Job? = null

    fun processIntent(intent: DataSimulatorIntent) {
        when (intent) {
            is DataSimulatorIntent.StartSimulation -> startSimulation(intent.intervalMs)
            is DataSimulatorIntent.StopSimulation -> stopSimulation()
            is DataSimulatorIntent.SetInterval -> setInterval(intent.intervalMs)
        }
    }

    private fun startSimulation(intervalMs: Long) {
        if (generationJob != null) return

        _state.update { it.copy(isGenerating = true, intervalMs = intervalMs) }

        generationJob = viewModelScope.launch {
            generateDataChunksUseCase.generateDataChunks(intervalMs)
                .collect { dataChunk ->
                    synchronizeDataUseCase.storeDataChunk(dataChunk)
                    _state.update { it.copy(generatedCount = it.generatedCount + 1) }
                }
        }
    }

    private fun stopSimulation() {
        generationJob?.cancel()
        generationJob = null
        _state.update { it.copy(isGenerating = false) }
    }

    private fun setInterval(intervalMs: Long) {
        _state.update { it.copy(intervalMs = intervalMs) }

        // If currently generating, restart with new interval
        if (_state.value.isGenerating) {
            stopSimulation()
            startSimulation(intervalMs)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulation()
    }
}
