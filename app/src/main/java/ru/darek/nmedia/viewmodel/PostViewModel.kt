package ru.darek.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.darek.nmedia.repository.PostRepository
import ru.darek.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    fun like() = repository.like()
    fun share() = repository.share()
}