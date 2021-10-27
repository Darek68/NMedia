package ru.darek.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
//import androidx.lifecycle.AndroidViewModel
import ru.darek.nmedia.db.AppDb
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.model.FeedModel
import ru.darek.nmedia.repository.*
import ru.darek.nmedia.util.SingleLiveEvent
import java.io.IOException
import ru.darek.nmedia.R
import ru.darek.nmedia.model.FeedModelState
import kotlin.concurrent.thread
import java.io.Closeable

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "404.png",
    likedByMe = false,
    published = "",
    likes = 0,
    share = 0,
    views = 0,
    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
   // упрощённый вариант
   private val repository: PostRepository =
       PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel>  = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            //_dataState.value = FeedModelState(refreshing = true)
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun likeById(id: Long) = viewModelScope.launch {
        val oldPost = data.value?.posts?.last { it.id == id }
        if (oldPost != null) {
            if (oldPost.likedByMe) {
                try {
                    //_dataState.value = FeedModelState(refreshing = true)
                    _dataState.value = FeedModelState(loading = true)
                    repository.dislikeById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
            else {
                try {
                    //_dataState.value = FeedModelState(refreshing = true)
                    _dataState.value = FeedModelState(loading = true)
                    repository.likeById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {

            //_dataState.value = FeedModelState(refreshing = true)
            _dataState.value = FeedModelState(loading = true)
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _dataState.value = FeedModelState(loading = true)
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun shareById(id: Long) {}

}


