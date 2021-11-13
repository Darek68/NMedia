package ru.darek.nmedia.application

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.work.RefreshPostsWorker
import java.util.concurrent.TimeUnit

/*
class NMediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
} */

class NMediaApplication : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        setupWork()
    }

    private fun setupAuth() {
        appScope.launch {
            AppAuth.initApp(this@NMediaApplication)
        }
    }

    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(this@NMediaApplication).enqueueUniquePeriodicWork(
                RefreshPostsWorker.name,
                ExistingPeriodicWorkPolicy.KEEP, // REPLACE - если одинаковые задачи совмещаем
                request
            )
        }
    }
}