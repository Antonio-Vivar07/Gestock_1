package com.example.uinavegacion.data.remote

import retrofit2.http.GET

interface ReportApiService {
    @GET("api/inventory/report")
    suspend fun getReport(): List<RemoteProductReport>
}
