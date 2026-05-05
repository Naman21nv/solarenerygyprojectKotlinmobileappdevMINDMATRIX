package com.suryashakti.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "energy_logs")
data class EnergyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val generation: Double, // kWh
    val consumption: Double, // kWh
    val batteryLevel: Int, // Percentage
    val weather: String // Sunny, Cloudy, Rainy
)
