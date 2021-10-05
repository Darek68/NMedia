package ru.darek.nmedia.repository

import ru.darek.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: Callback<List<Post>>)
    fun save(post: Post, callback: Callback<Post>)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun likeById(id: Long, callback: Callback<Post>)
    fun unlikeById(id: Long, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }
}
/*
interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun unlikeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
    fun getAllAsync(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
    fun likeByIdAsync(id: Long,callback: SaveCallback)
    fun unlikeByIdAsync(id: Long,callback: SaveCallback)
    fun saveAsync(post: Post,callback: SaveCallback)
    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
    fun removeByIdAsync(id: Long,callback: DeleteCallback)
    interface DeleteCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }
} */