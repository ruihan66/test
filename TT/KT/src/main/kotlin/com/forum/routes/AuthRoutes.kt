package com.forum.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.forum.models.*
import com.forum.services.AuthService
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.authRoutes() {
    val authService = AuthService()

    post("/auth/register") {
        try {
            val request = call.receive<RegisterRequest>()
            val result = authService.register(request)
            call.respond(HttpStatusCode.Created, result)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }

    post("/auth/login") {
        try {
            val request = call.receive<LoginRequest>()
            val result = authService.login(request)
            call.respond(HttpStatusCode.OK, result)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to e.message))
        }
    }

    authenticate("auth-jwt") {
        post("/auth/logout") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                authService.logout(userId!!)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}

