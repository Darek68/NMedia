package ru.darek.nmedia.repository


import androidx.lifecycle.Transformations
import ru.darek.nmedia.dao.PostDao
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.entity.PostEntity

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
//import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit


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

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
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
                        //callback.onSuccess(gson.fromJson(body, typeToken.type))
                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
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
                        //callback.onSuccess(gson.fromJson(body, typeToken.type))
                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
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
                        //callback.onSuccess(gson.fromJson(body, typeToken.type))
                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
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
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
    override fun shareById(id: Long) {
        // эта опция не реализована на сервере..
    }
}

