package ru.darek.nmedia.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.darek.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
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
    fun toDto() =  Post(id, author, authorAvatar ?: "", content, published, likedByMe, likes, share, views,video ?: "",newer)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes, dto.share, dto.views, dto.video)
       /* fun fromDto2(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes, dto.share, dto.views, dto.video, dto.newer) */

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
//fun List<Post>.toEntity2(): List<PostEntity> = map(PostEntity::fromDto2)
