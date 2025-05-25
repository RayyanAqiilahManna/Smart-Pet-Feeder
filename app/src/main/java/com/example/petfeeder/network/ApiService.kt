package com.example.petfeeder.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.HTTP

// Existing schedule request bodies
data class TokenBody(val token: String)

data class AddScheduleRequest(
    val token: String,
    val time: String,
    val portion: Int
)

data class DeleteScheduleRequest(
    val token: String,
    val schedule_id: String
)

// Schedule response DTOs
data class ScheduleDto(
    val id: String,
    val time: String,
    val portion: Int
)

data class GetSchedulesResponse(
    val status: String,
    val schedules: List<ScheduleDto>
)

// Existing common status response
data class StatusResponse(
    val status: String,
    val message: String
)

// === New Cat Profile requests & responses ===

// Add cat profile request
data class AddCatProfileRequest(
    val token: String,
    val name: String,
    val breed: String
)

// Delete cat profile request
data class DeleteCatProfileRequest(
    val token: String,
    val cat_id: String
)

// Cat profile DTO for responses
data class CatProfileDto(
    val id: String,
    val name: String,
    val breed: String
)

// Get cat profiles response
data class GetCatProfilesResponse(
    val status: String,
    val cats: List<CatProfileDto>
)

interface ApiService {
    @POST("/add-schedule")
    suspend fun addSchedule(@Body request: AddScheduleRequest): StatusResponse

    @POST("/get-schedules")
    suspend fun getSchedules(@Body request: TokenBody): GetSchedulesResponse

    @HTTP(method = "DELETE", path = "/delete-schedule", hasBody = true)
    suspend fun deleteSchedule(@Body request: DeleteScheduleRequest): StatusResponse

    @POST("/add-cat-profile")
    suspend fun addCatProfile(@Body request: AddCatProfileRequest): StatusResponse

    @POST("/get-cat-profiles")
    suspend fun getCatProfiles(@Body request: TokenBody): GetCatProfilesResponse

    @HTTP(method = "DELETE", path = "/delete-cat-profile", hasBody = true)
    suspend fun deleteCatProfile(@Body request: DeleteCatProfileRequest): StatusResponse
}
