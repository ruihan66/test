package com.forum.utils

import org.jetbrains.exposed.sql.ResultRow
import com.forum.models.*

fun ResultRow.toUserDTO(): UserDTO = UserDTO(
    id = this[Users.id].toString(),
    username = this[Users.username],
    email = this[Users.email],
    avatar = this[Users.avatar],
    bio = this[Users.bio],
    interests = this[Users.interests]?.split(",") ?: emptyList(),
    followersCount = this[Users.followersCount],
    followingCount = this[Users.followingCount],
    createdAt = this[Users.createdAt].toString()
)

