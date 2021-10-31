package ru.darek.nmedia.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import ru.darek.nmedia.api.PostsApi
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.auth.AuthState
import ru.darek.nmedia.error.ApiError
import ru.darek.nmedia.error.NetworkError
import ru.darek.nmedia.error.UnknownError
import java.io.IOException

class FrmViewModel: ViewModel() {

    fun getToken(name:String,pass:String) = viewModelScope.launch {
        try {
            val response = PostsApi.retrofitService.updateUser(name,pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            AppAuth.getInstance().setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}

class Auth(val id:Long,val token:String)