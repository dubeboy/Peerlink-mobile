package com.dubedivine.samples.injection.module

import android.app.Application
import android.content.Context
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.remote.MvpStarterService
import com.dubedivine.samples.data.remote.MvpStarterServiceFactory
import com.dubedivine.samples.injection.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val mApplication: Application) {

    @Provides
    internal fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    @ApplicationContext
    internal fun provideContext(): Context {
        return mApplication
    }

    @Provides
    @Singleton
    internal fun provideMvpStarterService(): MvpStarterService {
        return MvpStarterServiceFactory.makeStarterService()
    }

    @Provides
    @Singleton
    internal fun provideMvpPreferences(): PreferencesHelper {
        return PreferencesHelper(mApplication)
    }
}
