package com.forum.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.forum.models.*
import com.forum.services.UserService
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.userRoutes() {
    val userService = UserService()

    authenticate("auth-jwt") {
        // 獲取當前用戶資料
        get("/users/me") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val user = userService.getUserById(userId!!)
                call.respond(user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 更新個人資料
        put("/users/me") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val request = call.receive<UpdateProfileRequest>()
                val user = userService.updateProfile(userId!!, request)
                call.respond(user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 關注用戶
        post("/users/{id}/follow") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val currentUserId = principal?.payload?.getClaim("userId")?.asString()
                val targetUserId = call.parameters["id"]!!
                val result = userService.followUser(currentUserId!!, targetUserId)
                call.respond(result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 取消關注用戶
        delete("/users/{id}/follow") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val currentUserId = principal?.payload?.getClaim("userId")?.asString()
                val targetUserId = call.parameters["id"]!!
                val result = userService.unfollowUser(currentUserId!!, targetUserId)
                call.respond(result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }

    // 獲取用戶資料
    get("/users/{id}") {
        try {
            val userId = call.parameters["id"]!!
            val user = userService.getUserById(userId)
            call.respond(user)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
        }
    }
}

