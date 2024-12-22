package com.forum.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table() {
    val id = uuid("id").autoGenerate()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 100)
    val avatar = varchar("avatar", 255).nullable()
    val bio = text("bio").nullable()
    val interests = text("interests").nullable()
    val followersCount = integer("followers_count").default(0)
    val followingCount = integer("following_count").default(0)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Posts : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id)
    val title = varchar("title", 200)
    val content = text("content")
    val images = text("images").nullable()
    val likesCount = integer("likes_count").default(0)
    val commentsCount = integer("comments_count").default(0)
    val viewsCount = integer("views_count").default(0)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object Comments : Table() {
    val id = uuid("id").autoGenerate()
    val postId = uuid("post_id").references(Posts.id)
    val userId = uuid("user_id").references(Users.id)
    val content = text("content")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Messages : Table() {
    val id = uuid("id").autoGenerate()
    val senderId = uuid("sender_id").references(Users.id)
    val receiverId = uuid("receiver_id").references(Users.id)
    val content = text("content")
    val read = bool("read").default(false)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

