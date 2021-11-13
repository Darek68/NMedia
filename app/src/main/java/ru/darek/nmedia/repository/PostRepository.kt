package ru.darek.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.dto.*


interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun getNewerPosts()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(post: Post, upload: MediaUpload?): Long
    suspend fun processWork(id: Long)
}

