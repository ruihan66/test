package com.forum.utils

import kotlinx.serialization.Serializable
import io.ktor.server.sessions.*

@Serializable
data class UserSession(
    val userId: String
)

