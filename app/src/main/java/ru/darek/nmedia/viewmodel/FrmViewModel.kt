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
    /*  POST http://localhost:9999/api/users/authentication
           Content-Type: application/x-www-form-urlencoded

           login=student&pass=secret */
    fun getToken(name:String,pass:String) = viewModelScope.launch {
        println("Работает getToken $name  $pass")
        try {
            val response = PostsApi.retrofitService.updateUser(name,pass)
            println("1 $name  $pass")
            if (!response.isSuccessful) {
                println("2 ${response.code().toString()}  ${response.message()}")
                throw ApiError(response.code(), response.message())
            }
            println("3 $name  $pass")
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            println("4 $name  $pass")
            AppAuth.getInstance().setAuth(body.id, body.token)
            println("5 $name  $pass")
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}

class Auth(val id:Long,val token:String)