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

/**
 * MainActivity: The entry point of the application.
 * In a modern Android architecture (MVVM), the Activity serves as the 'View' container
 * that hosts the UI and connects it to the underlying data layers.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        /**
         * Persistence Layer Initialization:
         * We initialize the Room Database here. It's a singleton to ensure 
         * we only have one instance of the database across the app.
         */
        val database = SolarDatabase.getDatabase(this)
        
        /**
         * Repository Pattern:
         * The Repository acts as a single source of truth for data. 
         * It abstracts the data source (Room DAO) from the rest of the app, 
         * making the architecture more modular and easier to test.
         */
        val repository = EnergyRepository(database.energyLogDao())
        
        /**
         * ViewModel Initialization with Factory:
         * We use ViewModelProvider with a custom Factory to inject the Repository dependency.
         * The ViewModel survives configuration changes (like rotations), ensuring data 
         * isn't lost and unnecessary database calls are avoided.
         */
        val viewModel = ViewModelProvider(
            this,
            EnergyViewModelFactory(repository)
        )[EnergyViewModel::class.java]

        /**
         * UI Configuration:
         * enableEdgeToEdge() allows the app to use the entire screen real estate,
         * drawing behind system bars for a modern, immersive look.
         */
        enableEdgeToEdge()

        /**
         * Jetpack Compose Entry Point:
         * setContent defines the UI using Composable functions instead of XML.
         * We wrap everything in our custom Theme to maintain consistent styling.
         */
        setContent {
            SuryaShaktiTheme {
                // We pass the ViewModel to the DashboardScreen so it can observe data reactively.
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
