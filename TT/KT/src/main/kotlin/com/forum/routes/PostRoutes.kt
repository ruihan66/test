package com.forum.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.forum.models.*
import com.forum.services.PostService
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.postRoutes() {
    val postService = PostService()

    // 獲取貼文列表
    get("/posts") {
        try {
            val page = call.parameters["page"]?.toInt() ?: 1
            val size = call.parameters["size"]?.toInt() ?: 20
            val posts = postService.getPosts(page, size)
            call.respond(posts)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }

    authenticate("auth-jwt") {
        // 創建貼文
        post("/posts") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val request = call.receive<CreatePostRequest>()
                val post = postService.createPost(userId!!, request)
                call.respond(HttpStatusCode.Created, post)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 點讚/取消點讚
        post("/posts/{id}/like") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val postId = call.parameters["id"]!!
                val result = postService.toggleLike(userId!!, postId)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 添加評論
        post("/posts/{id}/comments") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val postId = call.parameters["id"]!!
                val request = call.receive<CreateCommentRequest>()
                val comment = postService.addComment(userId!!, postId, request)
                call.respond(HttpStatusCode.Created, comment)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }

    // 獲取特定貼文
    get("/posts/{id}") {
        try {
            val postId = call.parameters["id"]!!
            val post = postService.getPost(postId)
            call.respond(post)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
        }
    }

    // 獲取貼文評論
    get("/posts/{id}/comments") {
        try {
            val postId = call.parameters["id"]!!
            val comments = postService.getComments(postId)
            call.respond(comments)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
}

