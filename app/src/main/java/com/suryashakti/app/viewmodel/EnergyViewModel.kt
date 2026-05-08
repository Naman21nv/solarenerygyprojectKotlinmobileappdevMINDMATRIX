package com.suryashakti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.suryashakti.app.data.local.EnergyLog
import com.suryashakti.app.data.repository.EnergyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SavingsReport(
    val totalGeneration: Double = 0.0,
    val totalConsumption: Double = 0.0,
    val totalSavings: Double = 0.0, // Assuming a rate per kWh
    val avgIndependence: Int = 0,
    val daysCount: Int = 0
)

class EnergyViewModel(private val repository: EnergyRepository) : ViewModel() {

    private val electricityRate = 0.15 // $0.15 per kWh as a default

    val allLogs: StateFlow<List<EnergyLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestLog: StateFlow<EnergyLog?> = repository.latestLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val last30DaysReport: StateFlow<SavingsReport> = allLogs.map { logs ->
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        val filteredLogs = logs.filter { it.date >= thirtyDaysAgo }
        
        if (filteredLogs.isEmpty()) return@map SavingsReport()

        val totalGen = filteredLogs.sumOf { it.generation }
        val totalCons = filteredLogs.sumOf { it.consumption }
        val avgInd = filteredLogs.map { calculateIndependenceScore(it.generation, it.consumption) }.average().toInt()
        
        SavingsReport(
            totalGeneration = totalGen,
            totalConsumption = totalCons,
            totalSavings = totalGen * electricityRate,
            avgIndependence = avgInd,
            daysCount = filteredLogs.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavingsReport())

    fun addLog(generation: Double, consumption: Double, batteryLevel: Int, weather: String) {
        viewModelScope.launch {
            val log = EnergyLog(
                generation = generation,
                consumption = consumption,
                batteryLevel = batteryLevel,
                weather = weather
            )
            repository.insert(log)
        }
    }

    // Requirement 4: Simulation based on weather
    fun simulateDay(weather: String) {
        val generation = when (weather) {
            "Sunny" -> Random.nextDouble(15.0, 25.0)
            "Cloudy" -> Random.nextDouble(5.0, 12.0)
            "Rainy" -> Random.nextDouble(1.0, 4.0)
            else -> 0.0
        }
        val consumption = Random.nextDouble(8.0, 15.0)
        val battery = Random.nextInt(20, 100)
        addLog(generation, consumption, battery, weather)
    }

    fun calculateNetEnergy(gen: Double, cons: Double): Double = gen - cons
    
    fun calculateIndependenceScore(gen: Double, cons: Double): Int {
        if (cons <= 0) return 100
        return ((gen / cons) * 100).coerceIn(0.0, 100.0).toInt()
    }

    fun getSuggestion(gen: Double, weather: String): String {
        return when {
            gen > 15 -> "High Sun ☀️: Use heavy appliances (Laundry/EV Charging) now!"
            weather == "Cloudy" -> "Cloudy ☁️: Moderate generation. Shift usage to mid-day."
            weather == "Rainy" -> "Rainy 🌧️: Low output. Conserve battery for essential lighting."
            else -> "System active. Monitor battery levels for night usage."
        }
    }
}

class EnergyViewModelFactory(private val repository: EnergyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnergyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EnergyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
