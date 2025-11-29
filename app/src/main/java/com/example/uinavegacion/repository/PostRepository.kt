package com.example.uinavegacion.repository

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.data.remote.PostApiService

/**
 * Repositorio responsable de obtener los posts desde la API remota.
 */
class PostRepository(
    private val apiService: PostApiService
) {

    /**
     * Obtiene la lista de posts desde la API REST.
     */
    suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
    }
}
