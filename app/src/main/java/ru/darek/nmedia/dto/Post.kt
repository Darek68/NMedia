package ru.darek.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String ?,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes:Int,
    val share:Int,
    val views:Int,
    val video:String ?
)
