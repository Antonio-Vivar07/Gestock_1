package com.example.uinavegacion.repository

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.data.remote.PostApiService

/**
 * Repositorio que encapsula el acceso a la API remota de posts.
 */
class PostRepository(
    private val apiService: PostApiService
) {

    /**
     * Obtiene la lista de posts desde la API REST.
     */
    suspend fun getPosts(): List<Post> = apiService.getPosts()
}
