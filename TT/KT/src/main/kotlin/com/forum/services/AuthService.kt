package com.forum.services

import com.forum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class AuthService {
    private val secret = "your-secret-key" // 應從配置文件讀取
    
    fun register(request: RegisterRequest): Map<String, String> {
        // 檢查用戶名和郵箱是否已存在
        transaction {
            if (Users.select { Users.username eq request.username }.count() > 0) {
                throw IllegalArgumentException("Username already exists")
            }
            if (Users.select { Users.email eq request.email }.count() > 0) {
                throw IllegalArgumentException("Email already exists")
            }

            // 加密密碼
            val hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

            // 創建新用戶
            val userId = UUID.randomUUID()
            Users.insert {
                it[id] = userId
                it[username] = request.username
                it[email] = request.email
                it[password] = hashedPassword
                it[createdAt] = LocalDateTime.now()
            }

            // 生成 JWT token
            return mapOf(
                "token" to generateToken(userId.toString()),
                "userId" to userId.toString()
            )
        }
    }

    fun login(request: LoginRequest): Map<String, String> {
        return transaction {
            val user = Users.select { Users.username eq request.username }
                .singleOrNull() ?: throw IllegalArgumentException("User not found")

            val hashedPassword = user[Users.password]
            val passwordMatch = BCrypt.verifyer()
                .verify(request.password.toCharArray(), hashedPassword.toCharArray())
                .verified

            if (!passwordMatch) {
                throw IllegalArgumentException("Invalid password")
            }

            mapOf(
                "token" to generateToken(user[Users.id].toString()),
                "userId" to user[Users.id].toString()
            )
        }
    }

    fun logout(userId: String) {
        // 在實際應用中，可能需要使無效化 token
        // 這裡可以添加 token 到黑名單等邏輯
    }

    private fun generateToken(userId: String): String {
        return JWT.create()
            .withAudience("forum-app")
            .withIssuer("http://0.0.0.0:8080/")
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 小時
            .sign(Algorithm.HMAC256(secret))
    }
}
