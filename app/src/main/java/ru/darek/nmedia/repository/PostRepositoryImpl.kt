package ru.darek.nmedia.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import ru.darek.nmedia.entity.toEntity
import ru.darek.nmedia.enumeration.AttachmentType
import ru.darek.nmedia.error.*
import java.lang.NullPointerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
     private val dao: PostDao,
     private val apiService: PostsApiService,
     private val postWorkDao: PostWorkDao,
 ) : PostRepository {
   /*  override val data = dao.getAll()
         .map(List<PostEntity>::toDto)
         .flowOn(Dispatchers.Default) */
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { PostPagingSource(apiService) },
    ).flow

    override suspend fun getAll() {
        try {
            dao.getAll() // что будет результатом вызова функции ...?  LiveData<List<PostEntity>> и что с этим делать?
           // val response = PostsApi.retrofitService.getAll()
            val response = apiService.getAll()
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
     // полученный Post переводим в PostWorkEntity и запишем в БД. Вернем id этой записи
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
    override suspend fun removeWork(id: Long) {
        try { println("PostRepositoryImpl.removeWork >>> id = $id")
            removeById(id)
        } catch (e: NetworkError){
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
     override suspend fun processWork(id: Long) {
         try {
             val entity = postWorkDao.getById(id)
             if (entity.attachment?.url != null) {
                 val upload = MediaUpload(Uri.parse(entity.attachment?.url).toFile())
                 saveWithAttachment(entity.toDto(), upload)
             } else {
                 save(entity.toDto())
             }
             println(entity.id)
             postWorkDao.removeById(id)
         } catch (e: NullPointerException){
            // println("Ошибка!!! processWork >>> \n" + e.message + "\n" + e.toString())
             throw DbError
            // println("Это уже не выводим.. processWork >>> \n" + e.message + "\n" + e.toString())
         } catch (e: Exception) {
            // println("Ошибка!!! >>> \n" + e.message + "\n" + e.toString())
              throw UnknownError
         }
     }
    override suspend fun save(post: Post) {
        try {
            dao.insert(PostEntity.fromDto(post))
            //val response = PostsApi.retrofitService.save(post)
            val response = apiService.save(post)
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
            // val response = PostsApi.retrofitService.upload(media)
             val response = apiService.upload(media)
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
             //val response = PostsApi.retrofitService.uploadWithAttachment(media, content)
             val response = apiService.uploadWithAttachment(media,content)
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
            // val response = PostsApi.retrofitService.likeById(id)
             val response = apiService.likeById(id)
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
            // val response = PostsApi.retrofitService.dislikeById(id)
             val response = apiService.dislikeById(id)
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
             //val response = PostsApi.retrofitService.getNewer(id)
             val response = apiService.getNewer(id)
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
         try { println("PostRepositoryImpl.removeById >>> id = $id")
             dao.removeById(id)
             //val response = PostsApi.retrofitService.removeById(id)
             val response = apiService.removeById(id)
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

