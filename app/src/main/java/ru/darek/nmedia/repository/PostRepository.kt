package ru.darek.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.darek.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)

    fun getNewerCount(id: Long): Flow<Int>
}

