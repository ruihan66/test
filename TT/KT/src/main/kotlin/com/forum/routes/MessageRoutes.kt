package com.forum.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.forum.models.*
import com.forum.services.MessageService
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*

fun Route.messageRoutes() {
    val messageService = MessageService()

    authenticate("auth-jwt") {
        // 獲取聊天列表
        get("/messages") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val messages = messageService.getChatList(userId!!)
                call.respond(messages)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 獲取與特定用戶的聊天記錄
        get("/messages/{userId}") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val currentUserId = principal?.payload?.getClaim("userId")?.asString()
                val targetUserId = call.parameters["userId"]!!
                val messages = messageService.getMessages(currentUserId!!, targetUserId)
                call.respond(messages)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 發送消息
        post("/messages/{userId}") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val senderId = principal?.payload?.getClaim("userId")?.asString()
                val receiverId = call.parameters["userId"]!!
                val request = call.receive<SendMessageRequest>()
                val message = messageService.sendMessage(senderId!!, receiverId, request)
                call.respond(HttpStatusCode.Created, message)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 標記消息為已讀
        put("/messages/{userId}/read") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val currentUserId = principal?.payload?.getClaim("userId")?.asString()
                val senderId = call.parameters["userId"]!!
                messageService.markAsRead(currentUserId!!, senderId)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }

    // WebSocket 聊天端點
    webSocket("/ws/chat") {
        try {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                return@webSocket
            }

            messageService.connectUser(session.userId, this)

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val received = frame.readText()
                    val messageData = Json.decodeFromString<WebSocketMessage>(received)
                    messageService.handleWebSocketMessage(session.userId, messageData)
                }
            } finally {
                messageService.disconnectUser(session.userId)
            }
        } catch (e: Exception) {
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, e.message ?: "Error"))
        }
    }
}

