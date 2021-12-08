package ru.darek.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.darek.nmedia.dao.PostDao
import ru.darek.nmedia.db.AppDb
import ru.darek.nmedia.repository.PostRepository
import ru.darek.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshPostsWorker @Inject constructor(
    applicationContext: Context,
    params: WorkerParameters,
    private val appDb: AppDb,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val name = "ru.darek.work.RefreshPostsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val repository: PostRepository =
            PostRepositoryImpl(
                //AppDb.getInstance(context = applicationContext).postDao(),
                appDb.postDao(),
                //AppDb.getInstance(context = applicationContext).postWorkDao(),
                appDb.postWorkDao()
            )

        try {
            repository.getAll()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}