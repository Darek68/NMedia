package ru.darek.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.darek.nmedia.db.AppDb
import ru.darek.nmedia.error.DbError
import ru.darek.nmedia.repository.PostRepository
import ru.darek.nmedia.repository.PostRepositoryImpl

class RemovePostWorker(
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "postId"
    }

    override suspend fun doWork(): Result {
        // извлекаем id из data (.setInputData(data) => inputData.)
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        println("RemovePost >>> id = $id")
        val repository: PostRepository =
            PostRepositoryImpl(
                AppDb.getInstance(context = applicationContext).postDao(),
                AppDb.getInstance(context = applicationContext).postWorkDao(),
            )
        return try {
            repository.removeWork(id)
            Result.success()
        } catch (e: DbError){
            // println("Ошибка!!! doWork() >>> \n" + e.message + "\n" + e.toString())
            Result.failure()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}