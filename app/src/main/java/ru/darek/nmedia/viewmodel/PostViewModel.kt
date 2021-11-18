package ru.darek.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.db.AppDb
import ru.darek.nmedia.dto.MediaUpload
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.model.FeedModel
import ru.darek.nmedia.repository.*
import ru.darek.nmedia.util.SingleLiveEvent
import ru.darek.nmedia.model.FeedModelState
import ru.darek.nmedia.work.RemovePostWorker
import ru.darek.nmedia.work.SavePostWorker
import ru.netology.nmedia.model.PhotoModel
import java.io.File

private val empty = Post(
    id = 0,
    content = "",
    authorId = 0,
    author = "",
    authorAvatar = "404.png",
    likedByMe = false,
    published = "",
    likes = 0,
    share = 0,
    views = 0,
    video = ""
)
private val noPhoto = PhotoModel()

class PostViewModel(application: Application) : AndroidViewModel(application) {
   // упрощённый вариант
   private val repository: PostRepository =
       PostRepositoryImpl(
           AppDb.getInstance(context = application).postDao(),
           AppDb.getInstance(context = application).postWorkDao(),
       )
    private val workManager: WorkManager =
        WorkManager.getInstance(application)

    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

   /* val data: LiveData<FeedModel>  = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default) */
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            //.catch { e -> _dataState.postValue(FeedModelState(error = true)) }
            .asLiveData(Dispatchers.Default)
    }
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    var newerCntSum: Int = 0

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo
    init {
        loadPosts()
    }
    fun removeById(id: Long) = viewModelScope.launch {
        try { println("PostVieModel >>> id = $id")
            _dataState.value = FeedModelState(loading = true)
            //Запихнем id в data и через setInputData передадим в Task
            val data = workDataOf(RemovePostWorker.postKey to id)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(request)
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
                    // запишем в БД и получим id
                    val id = repository.saveWork(
                        it, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    //Запихнем id в data и через setInputData передадим в Task
                    val data = workDataOf(SavePostWorker.postKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }
    fun saveOld() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _dataState.value = FeedModelState(loading = true)
                   // repository.save(it)
                    when(_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun loadNewPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getNewerPosts()
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

    fun removeByIdOld(id: Long) = viewModelScope.launch {
        try {

            //_dataState.value = FeedModelState(refreshing = true)
            _dataState.value = FeedModelState(loading = true)
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
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
    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun shareById(id: Long) {}

}


