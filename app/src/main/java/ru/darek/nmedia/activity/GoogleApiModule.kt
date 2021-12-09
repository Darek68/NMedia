package ru.darek.nmedia.activity

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GoogleApiModule {

    @Singleton
    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context,
    ): GoogleApiAvailability = GoogleApiAvailability.getInstance()
}