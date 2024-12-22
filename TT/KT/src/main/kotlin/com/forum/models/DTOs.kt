package com.forum.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserDTO(
    val id: String,
    val username: String,
    val email: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    val interests: List<String> = emptyList(),
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val createdAt: String
)

@Serializable
data class PostDTO(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val images: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class MessageDTO(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val read: Boolean = false,
    val createdAt: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
