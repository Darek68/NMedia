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
   /* private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    private val _data = repository.data.map{ FeedModel(posts = it, empty = it.isEmpty())}
    val data: LiveData<FeedModel>
        get() = _data
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated */
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
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
       /* edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty */
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

    fun likeById(id: Long) {
        TODO()
    }

    fun removeById(id: Long) {
        TODO()
    }
    fun shareById(id: Long) {}

}
    /*
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()

    }
    fun loadPosts() {

        viewModelScope.launch {
            _data.value = FeedModel(loading = true)
            val posts = repository.getAll()
            _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
        }
    }
    fun likeById(id: Long) {
      /*  val oldPost = _data.value?.posts?.last { it.id == id }
        if (oldPost != null) {
            if (oldPost.likedByMe){
                repository.unlikeById(id,object : PostRepository.Callback<Post> {
                    override fun onSuccess(newPost: Post) {
                        _data.postValue(
                            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                                .map {
                                    if (it.id == newPost.id) {
                                        newPost
                                    } else it
                                }
                            )
                        )
                    }
                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
                })
            }
            else {
                repository.likeById(id,object : PostRepository.Callback<Post> {
                    override fun onSuccess(newPost: Post) {
                        _data.postValue(
                            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                                .map {
                                    if (it.id == newPost.id) {
                                        newPost
                                    } else it
                                }
                            )
                        )
                    }
                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
                })
            }
        }*/
    }
    fun save() {
      /*  edited.value?.let {
            //  val oldPost = _data.value?.posts?.last { it.id == id }
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(newPost: Post) {
                    _data.postValue(
                        if (_data.value?.posts?.find { it.id == newPost.id } != null) {
                            _data.value?.copy(loading = false, posts = _data.value?.posts.orEmpty()
                                .map { if (it.id == newPost.id) newPost else it }
                            )
                        } else {
                            _data.value?.copy(loading = false, posts = _data.value?.posts.orEmpty().toMutableList() + newPost)
                        }
                    )
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
            _postCreated.postValue(Unit)
        }
        edited.value = empty */
    }

    fun removeById(id: Long) {
      /*  val old = _data.value?.posts.orEmpty()
        try {
            //repository.removeById(id)
            _data.value = FeedModel(loading = true)
            repository.removeById(id, object : PostRepository.Callback<Unit> {
                override fun onSuccess(posts: Unit) {
                    loadPosts()
                }
                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        } catch (e: IOException) {
            _data.postValue(_data.value?.copy(posts = old))
        } */
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
    } */

