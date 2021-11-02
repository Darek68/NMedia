package ru.darek.nmedia.viewmodel

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import ru.darek.nmedia.R
import ru.darek.nmedia.api.PostsApi
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.auth.AuthState
import ru.darek.nmedia.error.ApiError
import ru.darek.nmedia.error.NetworkError
import ru.darek.nmedia.error.UnknownError
import ru.darek.nmedia.model.FeedModelState
import ru.darek.nmedia.util.SingleLiveEvent
import java.io.IOException

class FrmViewModel: ViewModel() {
    //val data = MutableLiveData<Int>()
    val data = SingleLiveEvent<Int>()
    fun getToken(name:String,pass:String) {
        viewModelScope.launch {
            println("Работает getToken $name  $pass")
            try {
                val response = PostsApi.retrofitService.updateUser(name, pass)
                if (!response.isSuccessful) {
                    println("2 ${response.code().toString()}  ${response.message()}")
                    data.postValue(response.code())
                    return@launch
                    //  throw ApiError(response.code(), response.message())
                }
                println("3 response")
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                AppAuth.getInstance().setAuth(body.id, body.token)
                data.postValue(0)
                println("5 ${body.id}  ${body.token}")
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        }
    }
  /* fun getToken(name:String,pass:String) = viewModelScope.launch {
       println("Работает getToken $name  $pass")
       try {
           val response = PostsApi.retrofitService.updateUser(name,pass)
           if (!response.isSuccessful) {
               println("2 ${response.code().toString()}  ${response.message()}")
               if (response.code() == 404) {

               }
               throw ApiError(response.code(), response.message())
           }
           println("3 response")
           val body = response.body() ?: throw ApiError(response.code(), response.message())
           println("4     ${body.toString()} ")
           AppAuth.getInstance().setAuth(body.id, body.token)
           println("5 ${body.id}  ${body.token}")
       } catch (e: IOException) {
           throw NetworkError
       } catch (e: Exception) {
           throw UnknownError
       }
   } */
}

class Auth(val id:Long,val token:String)