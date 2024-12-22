package com.forum.services

import com.forum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import java.util.concurrent.ConcurrentHashMap

class MessageService {
    private val connections = ConcurrentHashMap<String, WebSocketSession>()

    fun getChatList(userId: String): List<ChatListItem> {
        return transaction {
            // 獲取最新消息作為聊天列表
            (Messages.select {
                (Messages.senderId eq UUID.fromString(userId)) or
                (Messages.receiverId eq UUID.fromString(userId))
            })
            .orderBy(Messages.createdAt, SortOrder.DESC)
            .map { it.toChatListItem() }
            .distinctBy { it.userId }
        }
    }

    fun getMessages(currentUserId: String, targetUserId: String): List<MessageDTO> {
        return transaction {
            Messages.select {
                ((Messages.senderId eq UUID.fromString(currentUserId)) and
                 (Messages.receiverId eq UUID.fromString(targetUserId))) or
                ((Messages.senderId eq UUID.fromString(targetUserId)) and
                 (Messages.receiverId eq UUID.fromString(currentUserId)))
            }
            .orderBy(Messages.createdAt)
            .map { it.toDTO() }
        }
    }

    fun sendMessage(senderId: String, receiverId: String, request: SendMessageRequest): MessageDTO {
        val message = transaction {
            val messageId = UUID.randomUUID()
            Messages.insert {
                it[id] = messageId
                it[Messages.senderId] = UUID.fromString(senderId)
                it[Messages.receiverId] = UUID.fromString(receiverId)
                it[content] = request.content
                it[read] = false
                it[createdAt] = LocalDateTime.now()
            }
            
            Messages.select { Messages.id eq messageId }
                .single()
                .toDTO()
        }

        // 如果接收者在線，通過WebSocket發送消息
        connections[receiverId]?.let { session ->
            val json = Json.encodeToString(MessageDTO.serializer(), message)
            launch {
                session.send(Frame.Text(json))
            }
        }

        return message
    }

    fun markAsRead(currentUserId: String, senderId: String) {
        transaction {
            Messages.update({
                (Messages.senderId eq UUID.fromString(senderId)) and
                (Messages.receiverId eq UUID.fromString(currentUserId)) and
                (Messages.read eq false)
            }) {
                it[read] = true
            }
        }
    }

    fun connectUser(userId: String, session: WebSocketSession) {
        connections[userId] = session
    }

    fun disconnectUser(userId: String) {
        connections.remove(userId)
    }

    fun handleWebSocketMessage(userId: String, message: WebSocketMessage) {
        // 處理WebSocket消息
        when (message.type) {
            "message" -> {
                val request = SendMessageRequest(message.content)
                sendMessage(userId, message.receiverId, request)
            }
            "typing" -> {
                // 處理正在輸入狀態
                connections[message.receiverId]?.let { session ->
                    launch {
                        session.send(Frame.Text(Json.encodeToString(message)))
                    }
                }
            }
        }
    }

    private fun ResultRow.toDTO(): MessageDTO = MessageDTO(
        id = this[Messages.id].toString(),
        senderId = this[Messages.senderId].toString(),
        receiverId = this[Messages.receiverId].toString(),
        content = this[Messages.content],
        read = this[Messages.read],
        createdAt = this[Messages.createdAt].toString()
    )
}
