package ru.darek.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okio.IOException
import ru.darek.nmedia.api.*
import ru.darek.nmedia.dao.PostDao
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.entity.PostEntity

import ru.darek.nmedia.entity.toDto
import ru.darek.nmedia.entity.toEntity
//import ru.darek.nmedia.entity.toEntity2
import ru.darek.nmedia.error.*

 class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
     override val data = dao.getAll()
         .map(List<PostEntity>::toDto)
         .flowOn(Dispatchers.Default)
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
     override suspend fun getNewerPosts() {
         try {
             dao.cancelNew()
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

     override fun getNewerCount(id: Long): Flow<Int> = flow {
         while (true) {
             delay(10_000L)
             println("Запрос сервера:  ${id}")
             val response = PostsApi.retrofitService.getNewer(id)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }
             val body = response.body() ?: throw ApiError(response.code(), response.message())
             dao.insert(body.toEntity())
            /* val posts = body.map {it.copy(newer = true)}
             dao.insert(posts.toEntity()) */
             emit(body.size)
         }
     }
         .catch { e -> throw AppError.from(e) }
         .flowOn(Dispatchers.Default)

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

