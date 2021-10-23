package ru.darek.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.create
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.darek.nmedia.BuildConfig
import ru.darek.nmedia.dto.Post

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
//private const val BASE_URL = "http://10.0.2.2:9999/api/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()


interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>
}
/*
interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): List<Post>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Post

    @POST("posts")
    suspend fun save(@Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Unit

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Post

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Post
} */

object PostsApi {
    val retrofitService by lazy {
        retrofit.create<PostsApiService>()
    }
}