package ru.darek.nmedia.repository

import androidx.lifecycle.*
import okio.IOException
import ru.darek.nmedia.api.*
import ru.darek.nmedia.dao.PostDao
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.entity.PostEntity

import ru.darek.nmedia.entity.toDto
import ru.darek.nmedia.entity.toEntity
import ru.darek.nmedia.error.*

 class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
     override val data = dao.getAll().map(List<PostEntity>::toDto)
    override suspend fun getAll() {
        try {
            dao.getAll() // что будет результатом вызова функции ...?  LiveData<List<PostEntity>> и что с этим делать?
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            dao.insert(PostEntity.fromDto(post))
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
     override suspend fun likeById(id: Long){
         try {
             dao.likeById(id)
             val response = PostsApi.retrofitService.likeById(id)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }
             val body = response.body() ?: throw ApiError(response.code(), response.message())
             dao.insert(PostEntity.fromDto(body))
         } catch (e: IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }
     }
     override suspend fun dislikeById(id: Long){
         try {
             dao.likeById(id)
             val response = PostsApi.retrofitService.dislikeById(id)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }
             val body = response.body() ?: throw ApiError(response.code(), response.message())
             dao.insert(PostEntity.fromDto(body))
         } catch (e: IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }
     }
     override suspend fun removeById(id:Long){
         try {
             dao.removeById(id)
             val response = PostsApi.retrofitService.removeById(id)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }
         } catch (e: IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }
     }
}

