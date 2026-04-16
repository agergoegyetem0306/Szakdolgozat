package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.gamifikalt_fitnessz_alkalmazas.network.ApiClient
import com.example.gamifikalt_fitnessz_alkalmazas.network.CreateCustomFoodRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.CreateFoodLogRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.FoodLogResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.FoodResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.FoodTodaySummaryResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.NutritionGoalOptionResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.NutritionGoalOptionsResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.UpdateNutritionGoalRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.UpdateNutritionGoalResponse
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressGreen
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressOrange
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressRed
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressYellow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ConsumptionScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var successText by remember { mutableStateOf<String?>(null) }

    var summary by remember { mutableStateOf<FoodTodaySummaryResponse?>(null) }
    var logs by remember { mutableStateOf<List<FoodLogResponse>>(emptyList()) }
    var foods by remember { mutableStateOf<List<FoodResponse>>(emptyList()) }

    var goalOptionsResponse by remember { mutableStateOf<NutritionGoalOptionsResponse?>(null) }
    var selectedGoalType by remember { mutableStateOf<String?>(null) }

    var selectedTab by remember { mutableStateOf(0) }
    var showAddLogDialog by remember { mutableStateOf(false) }
    var showAddCustomFoodDialog by remember { mutableStateOf(false) }

    fun loadData() {
        loading = true
        errorText = null

        ApiClient.create(context).getTodayFoodSummary()
            .enqueue(object : Callback<FoodTodaySummaryResponse> {
                override fun onResponse(
                    call: Call<FoodTodaySummaryResponse>,
                    response: Response<FoodTodaySummaryResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        summary = response.body()
                    } else {
                        errorText = "Az összegzés betöltése sikertelen"
                    }

                    ApiClient.create(context).getTodayFoodLogs()
                        .enqueue(object : Callback<List<FoodLogResponse>> {
                            override fun onResponse(
                                call: Call<List<FoodLogResponse>>,
                                response: Response<List<FoodLogResponse>>
                            ) {
                                if (response.isSuccessful && response.body() != null) {
                                    logs = response.body()!!
                                } else if (errorText == null) {
                                    errorText = "A mai fogyasztások betöltése sikertelen"
                                }

                                ApiClient.create(context).getFoods()
                                    .enqueue(object : Callback<List<FoodResponse>> {
                                        override fun onResponse(
                                            call: Call<List<FoodResponse>>,
                                            response: Response<List<FoodResponse>>
                                        ) {
                                            if (response.isSuccessful && response.body() != null) {
                                                foods = response.body()!!
                                            } else if (errorText == null) {
                                                errorText = "Az ételek betöltése sikertelen"
                                            }

                                            ApiClient.create(context).getNutritionGoalOptions()
                                                .enqueue(object : Callback<NutritionGoalOptionsResponse> {
                                                    override fun onResponse(
                                                        call: Call<NutritionGoalOptionsResponse>,
                                                        response: Response<NutritionGoalOptionsResponse>
                                                    ) {
                                                        loading = false
                                                        if (response.isSuccessful && response.body() != null) {
                                                            goalOptionsResponse = response.body()
                                                            selectedGoalType = response.body()!!.current_goal_type
                                                        } else if (errorText == null) {
                                                            errorText = "A célbeállítások betöltése sikertelen"
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<NutritionGoalOptionsResponse>,
                                                        t: Throwable
                                                    ) {
                                                        loading = false
                                                        if (errorText == null) {
                                                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                                        }
                                                    }
                                                })
                                        }

                                        override fun onFailure(
                                            call: Call<List<FoodResponse>>,
                                            t: Throwable
                                        ) {
                                            loading = false
                                            if (errorText == null) {
                                                errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                            }
                                        }
                                    })
                            }

                            override fun onFailure(
                                call: Call<List<FoodLogResponse>>,
                                t: Throwable
                            ) {
                                loading = false
                                if (errorText == null) {
                                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                }
                            }
                        })
                }

                override fun onFailure(call: Call<FoodTodaySummaryResponse>, t: Throwable) {
                    loading = false
                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                }
            })
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    if (showAddLogDialog) {
        AddFoodLogDialog(
            foods = foods,
            onDismiss = { showAddLogDialog = false },
            onSave = { foodId, quantityGrams ->
                ApiClient.create(context)
                    .createFoodLog(
                        CreateFoodLogRequest(
                            food_id = foodId,
                            quantity_grams = quantityGrams
                        )
                    )
                    .enqueue(object : Callback<FoodLogResponse> {
                        override fun onResponse(
                            call: Call<FoodLogResponse>,
                            response: Response<FoodLogResponse>
                        ) {
                            if (response.isSuccessful) {
                                showAddLogDialog = false
                                successText = "Fogyasztás sikeresen rögzítve"
                                loadData()
                            } else {
                                errorText = "A fogyasztás mentése sikertelen"
                            }
                        }

                        override fun onFailure(call: Call<FoodLogResponse>, t: Throwable) {
                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                        }
                    })
            }
        )
    }

    if (showAddCustomFoodDialog) {
        AddCustomFoodDialog(
            onDismiss = { showAddCustomFoodDialog = false },
            onSave = { name, calories, protein, carbs, fat ->
                ApiClient.create(context)
                    .createCustomFood(
                        CreateCustomFoodRequest(
                            name = name,
                            calories_per_100g = calories,
                            protein_per_100g = protein,
                            carbs_per_100g = carbs,
                            fat_per_100g = fat
                        )
                    )
                    .enqueue(object : Callback<FoodResponse> {
                        override fun onResponse(
                            call: Call<FoodResponse>,
                            response: Response<FoodResponse>
                        ) {
                            if (response.isSuccessful) {
                                showAddCustomFoodDialog = false
                                errorText = null
                                successText = "Saját étel sikeresen hozzáadva"
                                loadData()
                            } else {
                                errorText = "A saját étel mentése sikertelen"
                            }
                        }

                        override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                        }
                    })
            }
        )
    }

    val calorieProgress = if ((summary?.daily_calorie_goal ?: 0) > 0) {
        ((summary?.total_calories ?: 0.0) / (summary?.daily_calorie_goal ?: 1)).toFloat()
    } else {
        0f
    }

    val protein = summary?.total_protein ?: 0.0
    val carbs = summary?.total_carbs ?: 0.0
    val fat = summary?.total_fat ?: 0.0

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
                text = "Mai fogyasztás",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kalória és tápanyagkövetés",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                ConsumptionDashboardCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorText != null) {
                ConsumptionDashboardCard {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (successText != null) {
                ConsumptionDashboardCard {
                    Text(
                        text = successText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            summary?.let {
                ConsumptionDashboardCard {
                    Text(
                        text = "Mai kalóriacél",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${it.total_calories} / ${it.daily_calorie_goal} kcal",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { calorieDisplayProgress(calorieProgress) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = calorieProgressColor(calorieProgress),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Mai bejegyzések: ${it.log_count}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MacroStatCard(
                        title = "Fehérje",
                        value = "${protein} g",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    MacroStatCard(
                        title = "Szénhidrát",
                        value = "${carbs} g",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    MacroStatCard(
                        title = "Zsír",
                        value = "${fat} g",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            ConsumptionDashboardCard {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Mai napló") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Ételek") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Cél") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        if (logs.isEmpty() && !loading) {
                            Text(
                                text = "Ma még nincs rögzített fogyasztás.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            logs.forEach { log ->
                                FoodLogItemCard(
                                    log = log,
                                    onDeleteClick = {
                                        errorText = null
                                        successText = null

                                        ApiClient.create(context)
                                            .deleteFoodLog(log.id)
                                            .enqueue(object : Callback<Map<String, Any>> {
                                                override fun onResponse(
                                                    call: Call<Map<String, Any>>,
                                                    response: Response<Map<String, Any>>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        successText = "A fogyasztási bejegyzés törölve lett"
                                                        loadData()
                                                    } else {
                                                        errorText = "A fogyasztás törlése sikertelen"
                                                    }
                                                }

                                                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                                }
                                            })
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                errorText = null
                                successText = null
                                showAddLogDialog = true
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
                            Text("Új fogyasztás", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    1 -> {
                        Button(
                            onClick = {
                                errorText = null
                                successText = null
                                showAddCustomFoodDialog = true
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
                            Text("Saját étel hozzáadása", style = MaterialTheme.typography.labelLarge)
                        }
                        if (foods.isEmpty() && !loading) {
                            Text(
                                text = "Nincs elérhető étel.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            foods.forEach { food ->
                                FoodItemCard(food = food)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    2 -> {
                        GoalTabContentStyled(
                            goalOptionsResponse = goalOptionsResponse,
                            selectedGoalType = selectedGoalType,
                            onGoalSelect = {
                                selectedGoalType = it
                                errorText = null
                                successText = null
                            },
                            onSave = { goalType ->
                                ApiClient.create(context)
                                    .updateNutritionGoal(UpdateNutritionGoalRequest(goal_type = goalType))
                                    .enqueue(object : Callback<UpdateNutritionGoalResponse> {
                                        override fun onResponse(
                                            call: Call<UpdateNutritionGoalResponse>,
                                            response: Response<UpdateNutritionGoalResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                successText = "Kalóriacél sikeresen mentve"
                                                loadData()
                                            } else {
                                                errorText = "A kalóriacél mentése sikertelen"
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<UpdateNutritionGoalResponse>,
                                            t: Throwable
                                        ) {
                                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                        }
                                    })
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                Text("Vissza", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ConsumptionDashboardCard(
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
fun MacroStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun FoodLogItemCard(
    log: FoodLogResponse,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.food?.name ?: "Ismeretlen étel",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Mennyiség: ${log.quantity_grams} g",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Fehérje: ${log.protein} g • Szénhidrát: ${log.carbs} g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Zsír: ${log.fat} g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        text = "${log.calories} kcal",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onDeleteClick) {
                        Text("Törlés")
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(food: FoodResponse) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kalória / 100 g: ${food.calories_per_100g} kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Fehérje / 100 g: ${food.protein_per_100g} g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Szénhidrát / 100 g: ${food.carbs_per_100g} g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Zsír / 100 g: ${food.fat_per_100g} g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (food.is_custom) "Saját étel" else "Alapélelmiszer",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GoalTabContentStyled(
    goalOptionsResponse: NutritionGoalOptionsResponse?,
    selectedGoalType: String?,
    onGoalSelect: (String) -> Unit,
    onSave: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (goalOptionsResponse == null) {
            Text("A céladatok még nem érhetők el.")
            return
        }

        Text(
            text = "Becsült fenntartó kalória: ${goalOptionsResponse.maintenance_calories} kcal",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (goalOptionsResponse.current_goal_type != null && goalOptionsResponse.current_calorie_target != null) {
            Text(
                text = "Jelenlegi cél: ${goalLabel(goalOptionsResponse.current_goal_type)} - ${goalOptionsResponse.current_calorie_target} kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        goalOptionsResponse.options.forEach { option ->
            GoalOptionCard(
                option = option,
                selected = selectedGoalType == option.goal_type,
                onClick = { onGoalSelect(option.goal_type) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (selectedGoalType != null) {
                    onSave(selectedGoalType)
                }
            },
            enabled = selectedGoalType != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Kalóriacél mentése", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun GoalOptionCard(
    option: NutritionGoalOptionResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else CardBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = goalLabel(option.goal_type),
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Napi cél: ${option.calorie_target} kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddFoodLogDialog(
    foods: List<FoodResponse>,
    onDismiss: () -> Unit,
    onSave: (foodId: Int, quantityGrams: Double) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<FoodResponse?>(null) }
    var quantityText by remember { mutableStateOf("") }

    var foodError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }

    val filteredFoods = foods.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Új fogyasztás") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Étel keresése") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    if (filteredFoods.isEmpty()) {
                        item {
                            Text("Nincs találat.")
                        }
                    }

                    items(filteredFoods) { food ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFood?.id == food.id) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    CardBackground
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedFood = food
                                    foodError = null
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = food.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedFood?.id == food.id) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Kalória / 100 g: ${food.calories_per_100g} kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (selectedFood?.id == food.id) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                selectedFood?.let {
                    Text("Kiválasztott étel: ${it.name}")
                }

                if (foodError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = foodError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it.replace(',', '.')
                        quantityError = null
                    },
                    label = { Text("Mennyiség (gramm)") },
                    singleLine = true,
                    isError = quantityError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (quantityError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = quantityError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    foodError = null
                    quantityError = null

                    val quantity = quantityText.toDoubleOrNull()
                    var valid = true

                    if (selectedFood == null) {
                        foodError = "Válassz ételt"
                        valid = false
                    }

                    if (quantity == null || quantity <= 0) {
                        quantityError = "Adj meg érvényes mennyiséget"
                        valid = false
                    }

                    if (!valid) return@Button

                    onSave(selectedFood!!.id, quantity!!)
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
fun AddCustomFoodDialog(
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }
    var proteinError by remember { mutableStateOf<String?>(null) }
    var carbsError by remember { mutableStateOf<String?>(null) }
    var fatError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saját étel hozzáadása") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Étel neve") },
                    singleLine = true,
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (nameError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = calories,
                    onValueChange = {
                        calories = it.replace(',', '.')
                        caloriesError = null
                    },
                    label = { Text("Kalória / 100 g") },
                    singleLine = true,
                    isError = caloriesError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (caloriesError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = caloriesError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = protein,
                    onValueChange = {
                        protein = it.replace(',', '.')
                        proteinError = null
                    },
                    label = { Text("Fehérje / 100 g") },
                    singleLine = true,
                    isError = proteinError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (proteinError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = proteinError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = carbs,
                    onValueChange = {
                        carbs = it.replace(',', '.')
                        carbsError = null
                    },
                    label = { Text("Szénhidrát / 100 g") },
                    singleLine = true,
                    isError = carbsError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (carbsError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = carbsError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fat,
                    onValueChange = {
                        fat = it.replace(',', '.')
                        fatError = null
                    },
                    label = { Text("Zsír / 100 g") },
                    singleLine = true,
                    isError = fatError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (fatError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fatError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    nameError = null
                    caloriesError = null
                    proteinError = null
                    carbsError = null
                    fatError = null

                    val caloriesValue = calories.toDoubleOrNull()
                    val proteinValue = protein.toDoubleOrNull()
                    val carbsValue = carbs.toDoubleOrNull()
                    val fatValue = fat.toDoubleOrNull()

                    var valid = true

                    if (name.isBlank()) {
                        nameError = "Add meg az étel nevét"
                        valid = false
                    }

                    if (caloriesValue == null || caloriesValue < 0) {
                        caloriesError = "Adj meg érvényes kalóriát"
                        valid = false
                    }

                    if (proteinValue == null || proteinValue < 0) {
                        proteinError = "Adj meg érvényes fehérjeértéket"
                        valid = false
                    }

                    if (carbsValue == null || carbsValue < 0) {
                        carbsError = "Adj meg érvényes szénhidrátértéket"
                        valid = false
                    }

                    if (fatValue == null || fatValue < 0) {
                        fatError = "Adj meg érvényes zsírértéket"
                        valid = false
                    }

                    if (!valid) return@Button

                    onSave(
                        name.trim(),
                        caloriesValue!!,
                        proteinValue!!,
                        carbsValue!!,
                        fatValue!!
                    )
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

fun goalLabel(goalType: String): String {
    return when (goalType) {
        "aggressive_cut" -> "Agresszív deficit"
        "modest_cut" -> "Mérsékelt deficit"
        "maintain" -> "Súlytartás"
        "modest_bulk" -> "Mérsékelt tömegnövelés"
        "aggressive_bulk" -> "Agresszív tömegnövelés"
        else -> goalType
    }
}

fun calorieDisplayProgress(progress: Float): Float {
    return progress.coerceIn(0f, 1f)
}

fun calorieProgressColor(progress: Float) = when {
    progress < 0.25f -> ProgressRed
    progress < 0.5f -> ProgressOrange
    progress < 0.75f -> ProgressYellow
    else -> ProgressGreen
}