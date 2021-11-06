package ru.darek.nmedia.dto

import ru.darek.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String ?,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes:Int,
    val share:Int,
    val views:Int,
    val video:String ?,
    val newer:Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)