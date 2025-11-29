package com.example.uinavegacion.data.remote

import retrofit2.http.GET

/**
 * Servicio Retrofit que define los endpoints para consumir la API de posts.
 */
interface PostApiService {

    @GET("posts")
    suspend fun getPosts(): List<Post>
}
