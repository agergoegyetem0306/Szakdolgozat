package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gamifikalt_fitnessz_alkalmazas.network.ActivityLogResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.ApiClient
import com.example.gamifikalt_fitnessz_alkalmazas.network.CreateActivityRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.TodaySummaryResponse
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressGreen
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressOrange
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressRed
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressYellow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ActivityScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var successText by remember { mutableStateOf<String?>(null) }

    var summary by remember { mutableStateOf<TodaySummaryResponse?>(null) }
    var activities by remember { mutableStateOf<List<ActivityLogResponse>>(emptyList()) }

    var selectedCategory by remember { mutableStateOf("cardio") }
    var showAddDialog by remember { mutableStateOf(false) }

    fun loadData() {
        loading = true
        errorText = null

        ApiClient.create(context).getTodaySummary()
            .enqueue(object : Callback<TodaySummaryResponse> {
                override fun onResponse(
                    call: Call<TodaySummaryResponse>,
                    response: Response<TodaySummaryResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        summary = response.body()
                    } else {
                        errorText = "Az összegzés betöltése sikertelen"
                    }

                    ApiClient.create(context).getTodayActivities()
                        .enqueue(object : Callback<List<ActivityLogResponse>> {
                            override fun onResponse(
                                call: Call<List<ActivityLogResponse>>,
                                response: Response<List<ActivityLogResponse>>
                            ) {
                                loading = false
                                if (response.isSuccessful && response.body() != null) {
                                    activities = response.body()!!
                                } else if (errorText == null) {
                                    errorText = "A mai aktivitások betöltése sikertelen"
                                }
                            }

                            override fun onFailure(
                                call: Call<List<ActivityLogResponse>>,
                                t: Throwable
                            ) {
                                loading = false
                                if (errorText == null) {
                                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                }
                            }
                        })
                }

                override fun onFailure(call: Call<TodaySummaryResponse>, t: Throwable) {
                    loading = false
                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                }
            })
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredActivities = activities.filter { it.category == selectedCategory }

    val totalPoints = summary?.total_points ?: 0.0
    val totalGoal = summary?.daily_goal ?: 200
    val totalProgress = if (totalGoal > 0) {
        (totalPoints / totalGoal).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val categoryPoints = when (selectedCategory) {
        "cardio" -> summary?.cardio_points ?: 0.0
        "strength" -> summary?.strength_points ?: 0.0
        else -> summary?.mental_points ?: 0.0
    }

    val categoryGoal = when (selectedCategory) {
        "cardio" -> summary?.cardio_goal ?: 100
        "strength" -> summary?.strength_goal ?: 80
        else -> summary?.mental_goal ?: 50
    }

    val categoryProgress = if (categoryGoal > 0) {
        (categoryPoints / categoryGoal).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    if (showAddDialog) {
        AddActivityDialog(
            category = selectedCategory,
            onDismiss = { showAddDialog = false },
            onSave = { activityType, intensity, durationMinutes ->
                ApiClient.create(context)
                    .createActivity(
                        CreateActivityRequest(
                            category = selectedCategory,
                            activity_type = activityType,
                            intensity = intensity,
                            duration_minutes = durationMinutes
                        )
                    )
                    .enqueue(object : Callback<ActivityLogResponse> {
                        override fun onResponse(
                            call: Call<ActivityLogResponse>,
                            response: Response<ActivityLogResponse>
                        ) {
                            if (response.isSuccessful) {
                                showAddDialog = false
                                successText = "Aktivitás sikeresen mentve"
                                loadData()
                            } else {
                                errorText = "Az aktivitás mentése sikertelen"
                            }
                        }

                        override fun onFailure(call: Call<ActivityLogResponse>, t: Throwable) {
                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                        }
                    })
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Mai aktivitás",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Napi mozgáskövetés és fejlődés",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                ActivityDashboardCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorText != null) {
                ActivityDashboardCard {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (successText != null) {
                ActivityDashboardCard {
                    Text(
                        text = successText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            ActivityDashboardCard {
                Text(
                    text = "Mai aktivitáscél",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$totalPoints / $totalGoal pont",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { totalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = activityProgressColor(totalProgress),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Mai bejegyzések: ${summary?.activity_count ?: 0}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ActivityDashboardCard {
                TabRow(selectedTabIndex = categoryIndex(selectedCategory)) {
                    Tab(
                        selected = selectedCategory == "cardio",
                        onClick = { selectedCategory = "cardio" },
                        text = { Text("Kardió") }
                    )
                    Tab(
                        selected = selectedCategory == "strength",
                        onClick = { selectedCategory = "strength" },
                        text = { Text("Erő") }
                    )
                    Tab(
                        selected = selectedCategory == "mental",
                        onClick = { selectedCategory = "mental" },
                        text = { Text("Mentális") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${categoryTitle(selectedCategory)} cél",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$categoryPoints / $categoryGoal pont",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { categoryProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = activityProgressColor(categoryProgress),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredActivities.isEmpty() && !loading) {
                ActivityDashboardCard {
                    Text(
                        text = "Ebben a kategóriában ma még nincs rögzített aktivitás.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                filteredActivities.forEach { activity ->
                    ActivityListCard(activity = activity)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorText = null
                    successText = null
                    showAddDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Új aktivitás",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = "Vissza",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ActivityDashboardCard(
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ActivityListCard(activity: ActivityLogResponse) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = readableActivityType(activity.activity_type),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Intenzitás: ${readableIntensity(activity.intensity)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Időtartam: ${activity.duration_minutes} perc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "${activity.points} pont",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun AddActivityDialog(
    category: String,
    onDismiss: () -> Unit,
    onSave: (activityType: String, intensity: String, durationMinutes: Int) -> Unit
) {
    val activityOptions = when (category) {
        "cardio" -> listOf("running", "walking", "cycling", "swimming")
        "strength" -> listOf("weightlifting", "bodyweight")
        "mental" -> listOf("meditation", "yoga", "reading")
        else -> emptyList()
    }

    var selectedType by remember(category) { mutableStateOf(activityOptions.firstOrNull() ?: "") }
    var selectedIntensity by remember { mutableStateOf("medium") }
    var durationText by remember { mutableStateOf("") }

    var typeError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Új ${categoryTitle(category)} aktivitás")
        },
        text = {
            Column {
                Text(
                    text = "Aktivitás típusa",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                activityOptions.forEach { option ->
                    val selected = selectedType == option

                    OutlinedButton(
                        onClick = {
                            selectedType = option
                            typeError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(readableActivityType(option))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (typeError != null) {
                    Text(
                        text = typeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Intenzitás",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    IntensityButton(
                        text = "Alacsony",
                        selected = selectedIntensity == "low",
                        onClick = { selectedIntensity = "low" },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IntensityButton(
                        text = "Közepes",
                        selected = selectedIntensity == "medium",
                        onClick = { selectedIntensity = "medium" },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IntensityButton(
                        text = "Magas",
                        selected = selectedIntensity == "high",
                        onClick = { selectedIntensity = "high" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = durationText,
                    onValueChange = {
                        durationText = it.filter { ch -> ch.isDigit() }
                        durationError = null
                    },
                    label = { Text("Időtartam (perc)") },
                    singleLine = true,
                    isError = durationError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (durationError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = durationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    typeError = null
                    durationError = null

                    val duration = durationText.toIntOrNull()
                    var valid = true

                    if (selectedType.isBlank()) {
                        typeError = "Válassz aktivitástípust"
                        valid = false
                    }

                    if (duration == null || duration <= 0) {
                        durationError = "Adj meg érvényes időtartamot"
                        valid = false
                    }

                    if (!valid) return@Button

                    onSave(selectedType, selectedIntensity, duration!!)
                },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Mentés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun IntensityButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text)
    }
}

fun categoryIndex(category: String): Int {
    return when (category) {
        "cardio" -> 0
        "strength" -> 1
        "mental" -> 2
        else -> 0
    }
}

fun categoryTitle(category: String): String {
    return when (category) {
        "cardio" -> "Kardió"
        "strength" -> "Erő"
        "mental" -> "Mentális"
        else -> category
    }
}

fun readableActivityType(type: String): String {
    return when (type) {
        "running" -> "Futás"
        "walking" -> "Séta"
        "cycling" -> "Biciklizés"
        "swimming" -> "Úszás"
        "weightlifting" -> "Súlyzós edzés"
        "bodyweight" -> "Saját testsúlyos edzés"
        "meditation" -> "Meditáció"
        "yoga" -> "Jóga"
        "reading" -> "Olvasás"
        else -> type
    }
}

fun readableIntensity(intensity: String): String {
    return when (intensity) {
        "low" -> "Alacsony"
        "medium" -> "Közepes"
        "high" -> "Magas"
        else -> intensity
    }
}

fun activityProgressColor(progress: Float) = when {
    progress < 0.25f -> ProgressRed
    progress < 0.5f -> ProgressOrange
    progress < 0.75f -> ProgressYellow
    else -> ProgressGreen
}