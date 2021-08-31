package ru.darek.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import androidx.lifecycle.*
//import androidx.lifecycle.AndroidViewModel
import ru.darek.nmedia.db.AppDb
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.repository.*
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
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    /* private val repository: PostRepository = PostRepositorySQLiteImpl(
         AppDb.getInstance(application).postDao
     ) */
    //private val repository: PostRepository = PostRepositoryInMemoryImpl()
    //private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)
    //private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data = repository.getAll()

    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
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

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}