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
    val video: String?,
) {
    fun toDto() =
        video?.let { Post(id, author, authorAvatar, content, published, likedByMe, likes, share, views, it) }

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes, dto.share, dto.views, dto.video)

    }
}
