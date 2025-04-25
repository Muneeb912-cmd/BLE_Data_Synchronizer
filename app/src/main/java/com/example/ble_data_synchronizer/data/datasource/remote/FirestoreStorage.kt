package com.example.ble_data_synchronizer.data.datasource.remote

import com.example.ble_data_synchronizer.data.model.DataChunk
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val dataChunksCollection = firestore.collection("data_chunks")

    suspend fun uploadDataChunk(dataChunk: DataChunk): Result<Unit> {
        return try {
            val dataMap = hashMapOf(
                "id" to dataChunk.id,
                "data" to dataChunk.data,
                "timestamp" to dataChunk.timestamp
            )

            dataChunksCollection.document(dataChunk.id)
                .set(dataMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
