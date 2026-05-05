package com.suryashakti.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.suryashakti.app.data.local.SolarDatabase
import com.suryashakti.app.data.repository.EnergyRepository
import com.suryashakti.app.ui.screens.DashboardScreen
import com.suryashakti.app.ui.theme.SuryaShaktiTheme
import com.suryashakti.app.viewmodel.EnergyViewModel
import com.suryashakti.app.viewmodel.EnergyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Database and Repository
        val database = SolarDatabase.getDatabase(this)
        val repository = EnergyRepository(database.energyLogDao())
        
        // Initialize ViewModel
        val viewModel = ViewModelProvider(
            this,
            EnergyViewModelFactory(repository)
        )[EnergyViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SuryaShaktiTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
