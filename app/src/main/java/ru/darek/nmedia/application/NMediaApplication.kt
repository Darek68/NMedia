package ru.darek.nmedia.application

import android.app.Application
import ru.darek.nmedia.auth.AppAuth


class NMediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}