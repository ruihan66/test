//output: src/main/kotlin/com/forum/plugins/Serialization.kt
package com.forum.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

//output: src/main/kotlin/com/forum/plugins/Monitoring.kt
package com.forum.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}

//output: src/main/kotlin/com/forum/plugins/WebSockets.kt
package com.forum.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

//output: src/main/kotlin/com/forum/models/Requests.kt
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

//output: src/main/kotlin/com/forum/services/FileService.kt
package com.forum.services

import java.io.File
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class FileService {
    private val uploadDir = "uploads"
    private val maxFileSize = 10L * 1024 * 1024 // 10MB

    init {
        Path(uploadDir).createDirectories()
        Path("$uploadDir/avatars").createDirectories()
        Path("$uploadDir/posts").createDirectories()
    }

    fun saveFile(bytes: ByteArray, filename: String, type: String): String {
        require(bytes.size <= maxFileSize) { "File size exceeds maximum limit" }
        
        val extension = filename.substringAfterLast('.', "")
        val newFilename = "${UUID.randomUUID()}.$extension"
        val path = "$uploadDir/$type/$newFilename"
        
        File(path).writeBytes(bytes)
        return "/files/$type/$newFilename"
    }

    fun deleteFile(path: String) {
        val file = File(path.removePrefix("/files/"))
        if (file.exists() && !file.isDirectory) {
            file.delete()
        }
    }
}

//output: src/main/kotlin/com/forum/utils/WebSocketSession.kt
package com.forum.utils

import kotlinx.serialization.Serializable
import io.ktor.server.sessions.*

@Serializable
data class UserSession(
    val userId: String
)

//output: src/main/kotlin/com/forum/utils/Extensions.kt
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

//output: src/main/resources/logback.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="trace">
        <appender-ref ref="STDOUT"/>
    </root>
    <logger name="org.eclipse.jetty" level="INFO"/>
    <logger name="io.netty" level="INFO"/>
</configuration>
