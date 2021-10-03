package ru.darek.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
//import androidx.lifecycle.AndroidViewModel
import ru.darek.nmedia.db.AppDb
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.model.FeedModel
import ru.darek.nmedia.repository.*
import ru.darek.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread
import java.io.Closeable

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes = 0,
    share = 0,
    views = 0,
    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
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

   /* fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    } */

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
    fun likeById(id: Long) {
        val oldPost = _data.value?.posts?.last { it.id == id }
            if (oldPost != null) {
                if (oldPost.likedByMe){
                    repository.unlikeByIdAsync(id,object : PostRepository.SaveCallback {
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
                    repository.likeByIdAsync(id,object : PostRepository.SaveCallback {
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
            }
    }
    fun save() {
        edited.value?.let {
            //  val oldPost = _data.value?.posts?.last { it.id == id }
            repository.saveAsync(it, object : PostRepository.SaveCallback {
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
            _postCreated.postValue(Unit)
        }
        edited.value = empty
    }
    fun saveOld() {
        edited.value?.let {
            thread {
                try {
                    repository.save(it)
                } catch (e: IOException) {
                    Log.d("TEST_ERROR", e.message.toString())
                    FeedModel(error = true)
                }
                _postCreated.postValue(Unit)
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

    fun likeByIdOld(id: Long) {
        val post = _data.value?.posts?.last { it.id == id }
        thread {
            if (post != null) {
                if (post.likedByMe) repository.unlikeById(id)
                else repository.likeById(id)
            }
        }
    }
    fun removeByIdOld(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        try {
            //repository.removeById(id)
            _data.value = FeedModel(loading = true)
            repository.removeByIdAsync(id, object : PostRepository.DeleteCallback {
                override fun onSuccess() {
                    loadPosts()
                }
                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        } catch (e: IOException) {
            _data.postValue(_data.value?.copy(posts = old))
        }
    }

    fun shareById(id: Long) = repository.shareById(id)
}
