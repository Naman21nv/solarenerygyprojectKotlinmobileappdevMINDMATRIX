package com.suryashakti.app.data.repository

import com.suryashakti.app.data.local.EnergyLog
import com.suryashakti.app.data.local.EnergyLogDao
import kotlinx.coroutines.flow.Flow

class EnergyRepository(private val energyLogDao: EnergyLogDao) {
    val allLogs: Flow<List<EnergyLog>> = energyLogDao.getAllLogs()
    val latestLog: Flow<EnergyLog?> = energyLogDao.getLatestLog()

    suspend fun insert(log: EnergyLog) {
        energyLogDao.insertLog(log)
    }

    suspend fun delete(log: EnergyLog) {
        energyLogDao.deleteLog(log)
    }
}
