package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.local.session.SessionManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Agrega:
 * - Authorization: Bearer <JWT>
 * - X-User-Info: <username>|<role>
 *
 * (El backend usa X-User-Info para auditoría y JWT para seguridad).
 */
class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val token = runBlocking { sessionManager.tokenFlow.firstOrNull() }
        val username = runBlocking { sessionManager.usernameFlow.firstOrNull() }
        val role = runBlocking { sessionManager.roleFlow.firstOrNull() }

        val reqBuilder = original.newBuilder()

        if (!token.isNullOrBlank()) {
            reqBuilder.addHeader("Authorization", "Bearer $token")
        }
        if (!username.isNullOrBlank() && !role.isNullOrBlank()) {
            reqBuilder.addHeader("X-User-Info", "$username|$role")
        }

        return chain.proceed(reqBuilder.build())
    }
}
