package com.forum.services

import com.forum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserService {
    fun getUserById(userId: String): UserDTO {
        return transaction {
            Users.select { Users.id eq UUID.fromString(userId) }
                .singleOrNull()
                ?.toDTO()
                ?: throw IllegalArgumentException("User not found")
        }
    }

    fun updateProfile(userId: String, request: UpdateProfileRequest): UserDTO {
        return transaction {
            Users.update({ Users.id eq UUID.fromString(userId) }) {
                request.bio?.let { bio -> it[Users.bio] = bio }
                request.interests?.let { interests -> 
                    it[Users.interests] = interests.joinToString(",")
                }
                request.avatar?.let { avatar -> it[Users.avatar] = avatar }
            }
            
            getUserById(userId)
        }
    }

    fun followUser(currentUserId: String, targetUserId: String): Map<String, Any> {
        if (currentUserId == targetUserId) {
            throw IllegalArgumentException("Cannot follow yourself")
        }

        return transaction {
            // 更新關注者和被關注者的計數
            Users.update({ Users.id eq UUID.fromString(currentUserId) }) {
                with(SqlExpressionBuilder) {
                    it.update(followingCount, followingCount + 1)
                }
            }

            Users.update({ Users.id eq UUID.fromString(targetUserId) }) {
                with(SqlExpressionBuilder) {
                    it.update(followersCount, followersCount + 1)
                }
            }

            mapOf("message" to "Successfully followed user")
        }
    }

    fun unfollowUser(currentUserId: String, targetUserId: String): Map<String, Any> {
        return transaction {
            // 更新關注者和被關注者的計數
            Users.update({ Users.id eq UUID.fromString(currentUserId) }) {
                with(SqlExpressionBuilder) {
                    it.update(followingCount, followingCount - 1)
                }
            }

            Users.update({ Users.id eq UUID.fromString(targetUserId) }) {
                with(SqlExpressionBuilder) {
                    it.update(followersCount, followersCount - 1)
                }
            }

            mapOf("message" to "Successfully unfollowed user")
        }
    }

    private fun ResultRow.toDTO(): UserDTO = UserDTO(
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
}
