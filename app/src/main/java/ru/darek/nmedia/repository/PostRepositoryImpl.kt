package ru.darek.nmedia.repository
//import okhttp3.Callback
//import okhttp3.OkHttpClient
//import okhttp3.Request

import androidx.lifecycle.*
import okio.IOException
//import retrofit2.Response.error
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
        //PostsApi.retrofitService.save(post)
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
             // сервак ничего не вернет.. :-(
            /* val body = response.body() ?: throw ApiError(response.code(), response.message())
             dao.insert(PostEntity.fromDto(body)) */
         } catch (e: IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }
     }
}
/*
class PostRepositoryImpl : PostRepository {
    // override val data = dao.getAll().map(List<PostEntity>::toDto)
    override suspend fun getAll():List<Post> = PostsApi.retrofitService.getAll()

    override suspend fun save(post: Post):Post = PostsApi.retrofitService.save(post)

    override suspend fun removeById(id: Long): Unit = PostsApi.retrofitService.removeById(id)

    override suspend fun likeById(id: Long):Post = PostsApi.retrofitService.dislikeById(id)
} */
/*
import java.util.concurrent.TimeUnit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.darek.nmedia.api.*
import ru.darek.nmedia.dto.Post
import java.lang.RuntimeException

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            var count = 2
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    if (count > 0) {
                        count--
                        PostsApi.retrofitService.getAll().enqueue(this)
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            var count = 2
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    if (count > 0) {
                        count--
                        PostsApi.retrofitService.save(post).enqueue(this)
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            var count = 2
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                        if (count > 0) {
                            count--
                            PostsApi.retrofitService.removeById(id).enqueue(this)
                        } else {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            var count = 2
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    if (count > 0) {
                        count--
                        PostsApi.retrofitService.likeById(id).enqueue(this)
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun unlikeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            var count = 2
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    if (count > 0) {
                        count--
                        PostsApi.retrofitService.likeById(id).enqueue(this)
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}*/


/*
class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .addInterceptor (HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        //private const val BASE_URL = "http://192.168.1.73:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        println(PostsApi.retrofitService.getAll()) // возвращает ссылку на функцию Call , от которой можем вызывать методы

        val request: Request = Request.Builder()
            //.url("${BASE_URL}/api/slow/posts")
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: error("body is null") //throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }
    override fun likeByIdAsync(id: Long,callback: PostRepository.SaveCallback) {
        //POST /api/posts/{id}/likes
        val request: Request = Request.Builder()
            .post(gson.toJson(id).toRequestBody()) // .post(gson.toJson(id).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: error("body is null") //throw RuntimeException("body is null")
                    try {
                        val modelPost = gson.fromJson(body, Post::class.java)
                        callback.onSuccess(modelPost)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun unlikeByIdAsync(id: Long,callback: PostRepository.SaveCallback) {
        //DELETE /api/posts/{id}/likes
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: error("body is null") //throw RuntimeException("body is null")
                    try {
                        val modelPost = gson.fromJson(body, Post::class.java)
                        callback.onSuccess(modelPost)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun saveAsync(post: Post,callback: PostRepository.SaveCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            //.url("${BASE_URL}/api/slow/posts")
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: error("body is null") //throw RuntimeException("body is null")
                    try {
                        val modelPost = gson.fromJson(body, Post::class.java)
                        callback.onSuccess(modelPost)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeByIdAsync(id: Long,callback: PostRepository.DeleteCallback) {
        val request: Request = Request.Builder()
            .delete()
            //.url("${BASE_URL}/api/slow/posts/$id")
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun shareById(id: Long) {
        // эта опция не реализована на сервере..
    }
    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            //.url("${BASE_URL}/api/slow/posts")
            .url("${BASE_URL}/api/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }
    override fun likeById(id: Long) {
        //POST /api/posts/{id}/likes
        val request: Request = Request.Builder()
            .post(gson.toJson(id).toRequestBody()) // .post(gson.toJson(id).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
    override fun unlikeById(id: Long) {
        //DELETE /api/posts/{id}/likes
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            //.url("${BASE_URL}/api/slow/posts")
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            //.url("${BASE_URL}/api/slow/posts/$id")
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}
*/
