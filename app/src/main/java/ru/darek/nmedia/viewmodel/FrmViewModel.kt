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
            try {
                val response = PostsApi.retrofitService.updateUser(name, pass)
                if (!response.isSuccessful) {
                    data.postValue(response.code())
                    return@launch
                    //  throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                AppAuth.getInstance().setAuth(body.id, body.token)
                data.postValue(0)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        }
    }
}

class Auth(val id:Long,val token:String)