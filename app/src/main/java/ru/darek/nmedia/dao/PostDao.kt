package ru.darek.nmedia.dao

import androidx.lifecycle.LiveData
import ru.darek.nmedia.dto.Post
import java.io.Closeable

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun likeById(id: Long)
    fun removeById(id: Long)
    fun shareById(id: Long)
}





