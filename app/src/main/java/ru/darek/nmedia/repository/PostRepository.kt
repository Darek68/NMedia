package ru.darek.nmedia.repository

import androidx.lifecycle.LiveData
import ru.darek.nmedia.dto.Post


interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
}
/*
interface PostRepository {
   /* val data: LiveData<List<Post>> */
    suspend fun getAll():List<Post>
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long): Post
    /* suspend fun unlikeById(id: Long): Post */

} */
