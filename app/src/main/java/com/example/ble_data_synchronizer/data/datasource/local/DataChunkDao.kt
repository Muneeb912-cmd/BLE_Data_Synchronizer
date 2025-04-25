package com.example.ble_data_synchronizer.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ble_data_synchronizer.data.model.DataChunk
import kotlinx.coroutines.flow.Flow

@Dao
interface DataChunkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataChunk(dataChunk: DataChunk)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataChunks(dataChunks: List<DataChunk>)

    @Query("SELECT * FROM data_chunks WHERE isUploaded = 0 ORDER BY timestamp ASC")
    fun getNotUploadedChunks(): Flow<List<DataChunk>>

    @Query("SELECT * FROM data_chunks ORDER BY timestamp DESC")
    fun getAllDataChunks(): Flow<List<DataChunk>>

    @Update
    suspend fun updateDataChunk(dataChunk: DataChunk)

    @Query("UPDATE data_chunks SET isUploaded = 1 WHERE id = :id")
    suspend fun markAsUploaded(id: String)
}