package ru.darek.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.darek.nmedia.BuildConfig
import ru.darek.nmedia.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiServiceModule {
    companion object{
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
    }

    @Provides
    @Singleton
    fun provideApiService(auth: AppAuth): PostsApiService {
        return retrofit(okhttp(loggingInterceptor(), authInterceptor(auth)))
            .create(PostsApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesOkhttp(
        appAuth: AppAuth,
        logging: HttpLoggingInterceptor
    ) : OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiServiceModule = retrofit.create()
}