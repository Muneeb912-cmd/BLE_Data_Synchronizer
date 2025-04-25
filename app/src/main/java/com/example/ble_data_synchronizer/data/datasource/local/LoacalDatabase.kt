package com.example.ble_data_synchronizer.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ble_data_synchronizer.data.model.DataChunk

@Database(entities = [DataChunk::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun dataChunkDao(): DataChunkDao
}