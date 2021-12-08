package ru.darek.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.darek.nmedia.api.PostsApiService
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.error.ApiError
import ru.darek.nmedia.error.NetworkError
import ru.darek.nmedia.error.UnknownError
import ru.darek.nmedia.util.SingleLiveEvent
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FrmViewModel @Inject constructor(
    private val auth: AppAuth,
    private val apiService: PostsApiService,): ViewModel() {
    //val data = MutableLiveData<Int>()
    val data = SingleLiveEvent<Int>()
    fun getToken(name:String,pass:String) {
        viewModelScope.launch {
            try {
                //val response = PostsApi.retrofitService.updateUser(name, pass)
                val response = apiService.updateUser(name,pass)
                if (!response.isSuccessful) {
                    data.postValue(response.code())
                    return@launch
                    //  throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                //AppAuth.getInstance().setAuth(body.id, body.token)
                auth.setAuth(body.id, body.token)
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