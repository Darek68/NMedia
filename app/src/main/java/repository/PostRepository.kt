package repository

import androidx.lifecycle.LiveData
import dto.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()
}