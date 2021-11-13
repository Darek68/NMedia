package ru.darek.nmedia.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.darek.nmedia.api.*
import ru.darek.nmedia.dao.PostDao
import ru.darek.nmedia.dao.PostWorkDao
import ru.darek.nmedia.dto.*
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.entity.PostEntity
import ru.darek.nmedia.entity.PostWorkEntity

import ru.darek.nmedia.entity.toDto
import ru.darek.nmedia.entity.toEntity
import ru.darek.nmedia.enumeration.AttachmentType
import ru.darek.nmedia.error.*

 class PostRepositoryImpl(
     private val dao: PostDao,
     private val postWorkDao: PostWorkDao,
 ) : PostRepository {
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
     override suspend fun saveWithAttachment(post: Post, uploadFile: MediaUpload) {
         try {
             val media = upload(uploadFile)
             // TODO: add support for other types
             val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
             save(postWithAttachment)
         } catch (e: AppError) {
             throw e
         } catch (e: java.io.IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }
     }

     override suspend fun upload(upload: MediaUpload): Media {
         try {
             val media = MultipartBody.Part.createFormData(
                 "file", upload.file.name, upload.file.asRequestBody()
             )

             val response = PostsApi.retrofitService.upload(media)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }

             return response.body() ?: throw ApiError(response.code(), response.message())
         } catch (e: java.io.IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }
     }
     suspend fun uploadWithAttachment(upload: MediaUpload): Media {
         try {
             val media = MultipartBody.Part.createFormData(
                 "file", upload.file.name, upload.file.asRequestBody()
             )
             val content = MultipartBody.Part.createFormData(
                 "content", "eny text"
             )

             val response = PostsApi.retrofitService.uploadWithAttachment(media, content)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }

             return response.body() ?: throw ApiError(response.code(), response.message())
         } catch (e: java.io.IOException) {
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
     override suspend fun saveWork(post: Post, upload: MediaUpload?): Long {
         try {
             val entity = PostWorkEntity.fromDto(post).apply {
                 if (upload != null) {
                     this.attachment?.url = upload.file.toUri().toString()
                 }
             }
             return postWorkDao.insert(entity)
         } catch (e: Exception) {
             throw UnknownError
         }
     }

     override suspend fun processWork(id: Long) {
         try {
             // TODO: handle this in homework
             val entity = postWorkDao.getById(id)
             if (entity.attachment?.url != null) {
                 val upload = MediaUpload(Uri.parse(entity.attachment?.url).toFile())
             }
             println(entity.id)
         } catch (e: Exception) {
             throw UnknownError
         }
     }
}

