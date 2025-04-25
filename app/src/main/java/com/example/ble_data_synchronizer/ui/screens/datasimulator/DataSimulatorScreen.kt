package com.example.ble_data_synchronizer.ui.screens.datasimulator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ble_data_synchronizer.data.model.DataChunk
import com.example.ble_data_synchronizer.utils.ConnectionStatus
import com.example.ble_data_synchronizer.utils.ConnectivityObserver
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DataSimulatorScreen(
    viewModel: DataSimulatorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Data Sync Simulator", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Data Generation: ${if(state.isGenerating)"On-Going" else "On-Hold"}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Wifi Status: ${state.connectivitySate}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Interval: ${state.intervalMs / 1000.0} seconds",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = state.intervalMs.toFloat(),
                onValueChange = {
                    viewModel.processIntent(DataSimulatorIntent.SetInterval(it.toLong()))
                },
                valueRange = 1000f..30000f,
                steps = 29
            )

            Row {
                OutlinedButton(onClick = {
                    viewModel.processIntent(DataSimulatorIntent.StartSimulation(state.intervalMs))
                }, modifier = Modifier.padding(10.dp)) { Text("Start") }
                OutlinedButton(onClick = {
                    viewModel.processIntent(DataSimulatorIntent.StopSimulation)
                }, modifier = Modifier.padding(10.dp)) { Text("Stop") }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isGenerating) {
                Text(
                    text = "Data chunks generated: ${state.generatedCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Text("Saved to Local Storage", fontSize = 18.sp, modifier = Modifier.padding(top=10.dp), fontWeight = FontWeight.SemiBold)
            if(state.connectivitySate==ConnectionStatus.UNAVAILABLE) Text("Pending Upload Chunks Count: ${state.pendingUploadCount}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.allDataChunks.filter { dataChunk -> !dataChunk.isUploaded }) { chunk ->
                    DataChunkDisplayCard(chunk)
                }
            }
            HorizontalDivider(thickness = 1.dp)
            Text("Uploaded to Cloud", fontSize = 18.sp, modifier = Modifier.padding(10.dp), fontWeight = FontWeight.SemiBold)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.allDataChunks.filter { dataChunk -> dataChunk.isUploaded }) { chunk ->
                    DataChunkDisplayCard(chunk)
                }
            }
        }
        Text("All-Right Reserved | 2025 ©CodingIsFun", fontSize = 15.sp)
    }
}

fun convertTimestampToTime(timestamp: Long, format: String = "HH:mm:ss"): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}

@Composable
private fun DataChunkDisplayCard(chunk: DataChunk) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
       Column(
           modifier = Modifier.fillMaxSize().height(80.dp),
       )  {
           Row{
               Text("ID: ${chunk.id}", modifier = Modifier
                   .padding(8.dp), fontWeight = FontWeight.Bold)
               Text(chunk.data, modifier = Modifier
                   .padding(8.dp)
                   .weight(1f))
               Text(
                   "Status: ${if (chunk.isUploaded) "Yes" else "No"}",
                   modifier = Modifier
                       .padding(8.dp), fontWeight = FontWeight.Bold)
           }
           Text("TimeStamp: ${convertTimestampToTime(chunk.timestamp, "dd/MM/yyyy HH:mm")}", modifier = Modifier
               .padding(8.dp)
               .weight(1f), fontWeight = FontWeight.Bold)
       }
    }
}
