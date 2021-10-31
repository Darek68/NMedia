package ru.darek.nmedia.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.darek.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String ?,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val share: Int,
    val views: Int,
    val video: String ?,
    val newer: Boolean = false,
) {
    fun toDto() =  Post(id, authorId, author, authorAvatar ?: "", content, published, likedByMe, likes, share, views,video ?: "",newer)

    companion object {
        fun fromDto(dto: Post, newer: Boolean = false) =
            PostEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes, dto.share, dto.views, dto.video,newer)
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(newer: Boolean = false): List<PostEntity> = map { PostEntity.fromDto(it, newer) }

