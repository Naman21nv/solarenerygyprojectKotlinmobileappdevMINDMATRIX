package com.suryashakti.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EnergyLogDao {
    @Query("SELECT * FROM energy_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<EnergyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EnergyLog)

    @Delete
    suspend fun deleteLog(log: EnergyLog)

    @Query("SELECT * FROM energy_logs ORDER BY date DESC LIMIT 1")
    fun getLatestLog(): Flow<EnergyLog?>
}
