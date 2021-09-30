package ru.darek.nmedia.repository

import androidx.lifecycle.LiveData
import ru.darek.nmedia.dto.Post

interface PostRepository {
    //fun getAll(): LiveData<List<Post>>
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun unlikeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
}