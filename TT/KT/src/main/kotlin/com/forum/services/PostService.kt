package com.forum.services

import com.forum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class PostService {
    fun getPosts(page: Int, size: Int): List<PostDTO> {
        return transaction {
            Posts.selectAll()
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .limit(size, offset = ((page - 1) * size).toLong())
                .map { it.toDTO() }
        }
    }

    fun createPost(userId: String, request: CreatePostRequest): PostDTO {
        return transaction {
            val postId = UUID.randomUUID()
            Posts.insert {
                it[id] = postId
                it[Posts.userId] = UUID.fromString(userId)
                it[title] = request.title
                it[content] = request.content
                it[images] = request.images.joinToString(",")
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            
            Posts.select { Posts.id eq postId }
                .single()
                .toDTO()
        }
    }

    fun toggleLike(userId: String, postId: String): Map<String, Any> {
        return transaction {
            val post = Posts.select { Posts.id eq UUID.fromString(postId) }
                .single()
            
            // 這裡應該有一個點讚表來記錄用戶的點讚狀態
            // 簡化版本直接返回成功
            Posts.update({ Posts.id eq UUID.fromString(postId) }) {
                with(SqlExpressionBuilder) {
                    it.update(likesCount, likesCount + 1)
                }
            }
            
            mapOf("liked" to true, "likesCount" to (post[Posts.likesCount] + 1))
        }
    }

    fun addComment(userId: String, postId: String, request: CreateCommentRequest): CommentDTO {
        return transaction {
            val commentId = UUID.randomUUID()
            Comments.insert {
                it[id] = commentId
                it[Comments.userId] = UUID.fromString(userId)
                it[Comments.postId] = UUID.fromString(postId)
                it[content] = request.content
                it[createdAt] = LocalDateTime.now()
            }
            
            // 更新貼文評論數
            Posts.update({ Posts.id eq UUID.fromString(postId) }) {
                with(SqlExpressionBuilder) {
                    it.update(commentsCount, commentsCount + 1)
                }
            }
            
            Comments.select { Comments.id eq commentId }
                .single()
                .toDTO()
        }
    }

    private fun ResultRow.toDTO(): PostDTO = PostDTO(
        id = this[Posts.id].toString(),
        userId = this[Posts.userId].toString(),
        title = this[Posts.title],
        content = this[Posts.content],
        images = this[Posts.images]?.split(",") ?: emptyList(),
        likesCount = this[Posts.likesCount],
        commentsCount = this[Posts.commentsCount],
        viewsCount = this[Posts.viewsCount],
        createdAt = this[Posts.createdAt].toString(),
        updatedAt = this[Posts.updatedAt].toString()
    )
}
