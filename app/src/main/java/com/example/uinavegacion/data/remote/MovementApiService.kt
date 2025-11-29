package com.example.uinavegacion.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface MovementApiService {

    @POST("api/movements")
    suspend fun createMovement(
        @Body movement: RemoteMovement
    )
}
