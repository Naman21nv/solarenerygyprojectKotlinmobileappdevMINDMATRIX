package com.suryashakti.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suryashakti.app.data.local.EnergyLog
import com.suryashakti.app.viewmodel.EnergyViewModel
import com.suryashakti.app.viewmodel.SavingsReport
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: EnergyViewModel) {
    val allLogs by viewModel.allLogs.collectAsState()
    val latestLog by viewModel.latestLog.collectAsState()
    val savingsReport by viewModel.last30DaysReport.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Surya-Shakti Solar") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EnergySummaryCard(latestLog, viewModel)
            }

            if (savingsReport.daysCount > 0) {
                item {
                    SavingsReportCard(savingsReport)
                }
            }
            
            item {
                Text("Recent Activity", style = MaterialTheme.typography.titleLarge)
            }

            items(allLogs) { log ->
                LogItem(log)
            }
        }

        if (showDialog) {
            AddLogDialog(
                onDismiss = { showDialog = false },
                onSave = { gen, cons, batt, weather ->
                    viewModel.addLog(gen, cons, batt, weather)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun SavingsReportCard(report: SavingsReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "30-Day Savings Report",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Saved", style = MaterialTheme.typography.labelSmall)
                    Text("$${"%.2f".format(report.totalSavings)}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Avg Independence", style = MaterialTheme.typography.labelSmall)
                    Text("${report.avgIndependence}%", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItemSmall("Total Gen", "${"%.1f".format(report.totalGeneration)} kWh")
                StatItemSmall("Total Cons", "${"%.1f".format(report.totalConsumption)} kWh")
                StatItemSmall("Days", "${report.daysCount}")
            }
        }
    }
}

@Composable
fun StatItemSmall(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EnergySummaryCard(log: EnergyLog?, viewModel: EnergyViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val gen = log?.generation ?: 0.0
            val cons = log?.consumption ?: 0.0
            val score = viewModel.calculateIndependenceScore(gen, cons)
            val net = viewModel.calculateNetEnergy(gen, cons)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Today's Independence Score", style = MaterialTheme.typography.labelSmall)
                    Text("$score%", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
                CircularProgressIndicator(
                    progress = score / 100f,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Generation", "${gen}kWh", MaterialTheme.colorScheme.primary)
                StatItem("Consumption", "${cons}kWh", MaterialTheme.colorScheme.tertiary)
                StatItem("Net", "${"%.1f".format(net)}kWh", if (net >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = viewModel.getSuggestion(gen, log?.weather ?: "Unknown"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleLarge, color = color)
    }
}

@Composable
fun LogItem(log: EnergyLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(Date(log.date).toString().substring(0, 10), fontWeight = FontWeight.Bold)
                Text("${log.weather} | Battery: ${log.batteryLevel}%", style = MaterialTheme.typography.labelSmall)
            }
            Text("+${log.generation} / -${log.consumption} kWh", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(onDismiss: () -> Unit, onSave: (Double, Double, Int, String) -> Unit) {
    var gen by remember { mutableStateOf("") }
    var cons by remember { mutableStateOf("") }
    var batt by remember { mutableStateOf("") }
    var weather by remember { mutableStateOf("Sunny") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Energy Data") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = gen, onValueChange = { gen = it }, label = { Text("Generation (kWh)") })
                TextField(value = cons, onValueChange = { cons = it }, label = { Text("Consumption (kWh)") })
                TextField(value = batt, onValueChange = { batt = it }, label = { Text("Battery Level (%)") })
                Text("Weather:")
                Row {
                    listOf("Sunny", "Cloudy", "Rainy").forEach { w ->
                        FilterChip(
                            selected = weather == w,
                            onClick = { weather = w },
                            label = { Text(w) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(gen.toDoubleOrNull() ?: 0.0, cons.toDoubleOrNull() ?: 0.0, batt.toIntOrNull() ?: 0, weather)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
