package com.forum.models

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val images: List<String> = emptyList()
)

@Serializable
data class CreateCommentRequest(
    val content: String
)

@Serializable
data class UpdateProfileRequest(
    val bio: String? = null,
    val interests: List<String>? = null,
    val avatar: String? = null
)

@Serializable
data class SendMessageRequest(
    val content: String
)

@Serializable
data class WebSocketMessage(
    val type: String,
    val receiverId: String,
    val content: String
)

