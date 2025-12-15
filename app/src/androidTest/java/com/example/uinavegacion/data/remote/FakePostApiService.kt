package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.data.remote.PostApiService

/**
 * Implementación falsa de [PostApiService] para pruebas.
 */
class FakePostApiService : PostApiService {

    /**
     * Devuelve una lista predefinida de posts para simular la respuesta de la API.
     */
    override suspend fun getPosts(): List<Post> {
        return listOf(
            Post(
                userId = 1,
                id = 1,
                title = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                body = "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            )
        )
    }
}
