package ru.darek.nmedia.repository

import androidx.lifecycle.LiveData
import ru.darek.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
}