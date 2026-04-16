package com.example.gamifikalt_fitnessz_alkalmazas.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val gender: String,
    val age: Int,
    val height: Int,
    val weight: Double
)

data class LoginRequest(val email: String, val password: String)

data class UserDto(val id: Int, val name: String, val email: String)
data class AuthResponse(val user: UserDto, val token: String)

data class ProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val gender: String?,
    val age: Int?,
    val height: Int?,
    val weight: Double?,
    val goal_type: String?,
    val calorie_target: Int?
)

data class UpdateProfileRequest(
    val gender: String?,
    val age: Int?,
    val height: Int?,
    val weight: Double?
)

data class CreateActivityRequest(
    val category: String,
    val activity_type: String,
    val intensity: String,
    val duration_minutes: Int
)

data class ActivityLogResponse(
    val id: Int,
    val user_id: Int,
    val category: String,
    val activity_type: String,
    val intensity: String,
    val duration_minutes: Int,
    val points: Double,
    val activity_date: String,
    val created_at: String?,
    val updated_at: String?
)

data class TodaySummaryResponse(
    val total_points: Double,
    val daily_goal: Int,
    val cardio_points: Double,
    val cardio_goal: Int,
    val strength_points: Double,
    val strength_goal: Int,
    val mental_points: Double,
    val mental_goal: Int,
    val activity_count: Int
)

data class FoodResponse(
    val id: Int,
    val name: String,
    val calories_per_100g: Double,
    val protein_per_100g: Double,
    val carbs_per_100g: Double,
    val fat_per_100g: Double,
    val is_custom: Boolean,
    val user_id: Int?
)

data class CreateCustomFoodRequest(
    val name: String,
    val calories_per_100g: Double,
    val protein_per_100g: Double,
    val carbs_per_100g: Double,
    val fat_per_100g: Double
)

data class CreateFoodLogRequest(
    val food_id: Int,
    val quantity_grams: Double
)

data class FoodLogResponse(
    val id: Int,
    val user_id: Int,
    val food_id: Int,
    val quantity_grams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val consumed_date: String,
    val created_at: String?,
    val updated_at: String?,
    val food: FoodResponse?
)

data class FoodTodaySummaryResponse(
    val total_calories: Double,
    val total_protein: Double,
    val total_carbs: Double,
    val total_fat: Double,
    val daily_calorie_goal: Int,
    val log_count: Int
)

data class NutritionGoalOptionResponse(
    val goal_type: String,
    val label: String,
    val calorie_target: Int
)

data class NutritionGoalOptionsResponse(
    val maintenance_calories: Int,
    val current_goal_type: String?,
    val current_calorie_target: Int?,
    val options: List<NutritionGoalOptionResponse>
)

data class UpdateNutritionGoalRequest(
    val goal_type: String
)

data class UpdateNutritionGoalResponse(
    val message: String,
    val goal_type: String,
    val calorie_target: Int,
    val maintenance_calories: Int
)

data class XpSummaryResponse(
    val xp: Int,
    val level: Int,
    val current_level_xp_min: Int,
    val next_level_xp: Int,
    val xp_in_level: Int,
    val xp_needed_for_next_level: Int,
    val today_activity_xp: Int,
    val today_nutrition_xp: Int,
    val today_total_xp: Int,
    val current_streak: Int,
    val best_streak: Int
)

data class WeeklyStatsDayResponse(
    val date: String,
    val activity_points: Double,
    val calories: Double,
    val xp_total: Int,
    val xp_activity: Int,
    val xp_nutrition: Int,
    val successful: Boolean
)

data class WeeklyStatsResponse(
    val days: List<WeeklyStatsDayResponse>,
    val weekly_activity_points: Double,
    val weekly_calories: Double,
    val weekly_xp: Int,
    val successful_days: Int
)
data class DailyChallengeInfoResponse(
    val id: Int,
    val title: String,
    val description: String,
    val challenge_type: String,
    val category: String?,
    val activity_type: String?,
    val target_value: Int,
    val reward_xp: Int
)

data class DailyChallengeProgressResponse(
    val current: Double,
    val target: Int,
    val ratio: Double
)

data class DailyChallengeResponse(
    val id: Int,
    val challenge_date: String,
    val is_completed: Boolean,
    val completed_at: String?,
    val reward_granted: Boolean,
    val progress: DailyChallengeProgressResponse,
    val challenge: DailyChallengeInfoResponse
)
data class CreateLeaderboardRequest(
    val name: String,
    val join_code: String?
)

data class JoinLeaderboardRequest(
    val join_code: String
)

data class LeaderboardListItemResponse(
    val id: Int,
    val name: String,
    val join_code: String,
    val created_by: Int,
    val members_count: Int
)

data class CreateLeaderboardResponse(
    val message: String,
    val leaderboard: LeaderboardListItemResponse
)

data class JoinLeaderboardResponse(
    val message: String,
    val leaderboard: LeaderboardListItemResponse
)

data class LeaderboardMemberEntryResponse(
    val rank: Int,
    val user_id: Int,
    val name: String,
    val weekly_xp: Int,
    val is_me: Boolean
)

data class LeaderboardInfoResponse(
    val id: Int,
    val name: String,
    val join_code: String,
    val created_by: Int,
    val creator_name: String?,
    val member_count: Int,
    val week_start: String,
    val week_end: String
)

data class LeaderboardDetailResponse(
    val leaderboard: LeaderboardInfoResponse,
    val my_rank: Int?,
    val members: List<LeaderboardMemberEntryResponse>
)

interface ApiService {

    @GET("daily-challenge")
    fun getDailyChallenge(): Call<DailyChallengeResponse>

    @GET("weekly-stats")
    fun getWeeklyStats(): Call<WeeklyStatsResponse>

    @POST("register")
    fun register(@Body req: RegisterRequest): Call<AuthResponse>

    @POST("login")
    fun login(@Body req: LoginRequest): Call<AuthResponse>

    @GET("me")
    fun me(): Call<UserDto>

    @GET("profile")
    fun getProfile(): Call<ProfileResponse>

    @PUT("profile")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<ProfileResponse>

    @POST("activities")
    fun createActivity(@Body request: CreateActivityRequest): Call<ActivityLogResponse>

    @GET("activities/today")
    fun getTodayActivities(): Call<List<ActivityLogResponse>>

    @GET("activities/today-summary")
    fun getTodaySummary(): Call<TodaySummaryResponse>

    @GET("foods")
    fun getFoods(@Query("search") search: String? = null): Call<List<FoodResponse>>

    @POST("foods/custom")
    fun createCustomFood(@Body request: CreateCustomFoodRequest): Call<FoodResponse>

    @POST("food-logs")
    fun createFoodLog(@Body request: CreateFoodLogRequest): Call<FoodLogResponse>

    @GET("food-logs/today")
    fun getTodayFoodLogs(): Call<List<FoodLogResponse>>

    @GET("food-logs/today-summary")
    fun getTodayFoodSummary(): Call<FoodTodaySummaryResponse>

    @GET("nutrition-goal/options")
    fun getNutritionGoalOptions(): Call<NutritionGoalOptionsResponse>

    @POST("nutrition-goal")
    fun updateNutritionGoal(@Body request: UpdateNutritionGoalRequest): Call<UpdateNutritionGoalResponse>

    @GET("xp-summary")
    fun getXpSummary(): Call<XpSummaryResponse>

    @GET("leaderboards")
    fun getLeaderboards(): Call<List<LeaderboardListItemResponse>>

    @POST("leaderboards")
    fun createLeaderboard(@Body request: CreateLeaderboardRequest): Call<CreateLeaderboardResponse>

    @POST("leaderboards/join")
    fun joinLeaderboard(@Body request: JoinLeaderboardRequest): Call<JoinLeaderboardResponse>

    @GET("leaderboards/{id}")
    fun getLeaderboardDetail(@retrofit2.http.Path("id") id: Int): Call<LeaderboardDetailResponse>

    @DELETE("activities/{id}")
    fun deleteActivity(@retrofit2.http.Path("id") id: Int): Call<Map<String, Any>>

    @DELETE("food-logs/{id}")
    fun deleteFoodLog(@retrofit2.http.Path("id") id: Int): Call<Map<String, Any>>
}