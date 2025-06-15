package gmo.demo.voidtask

import LocaleHelper
import android.app.Application
import android.content.Context
import android.os.Process
import gmo.demo.voidtask.data.db.AppDatabase
import gmo.demo.voidtask.data.network.AppAPI
import gmo.demo.voidtask.data.network.NetworkConnectionInterceptor
import gmo.demo.voidtask.data.network.services.AppServives
import gmo.demo.voidtask.data.repositories.AppRepository
import gmo.demo.voidtask.data.repositories.TaskRepository
import gmo.demo.voidtask.ui.addtask.AddTaskViewModelFactory
import gmo.demo.voidtask.ui.home.HomeViewModelFactory
import gmo.demo.voidtask.ui.main.MainScreenViewModelFactory
import gmo.demo.voidtask.ui.splash.SplashViewModelFactory
import gmo.demo.voidtask.ui.addVocab.AddVocabViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class LockerApplication : Application(), KodeinAware {

    /**
     * root handling of exception
     */
    private val unCaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, _ ->
        try {
            Process.killProcess(Process.myPid())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(unCaughtExceptionHandler)
    }

    override val kodein = Kodein.lazy {
        import(androidXModule(this@LockerApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { AppAPI.createRetrofit(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { instance<AppDatabase>().getFileEntryDao() }

        //bind Service
        bind() from provider { AppServives.create(instance()) }

        //bind Repository
        bind() from singleton { AppRepository(instance()) }
        bind() from singleton { TaskRepository(instance()) }

        //bind Factory
        bind() from provider { SplashViewModelFactory() }
        bind() from provider { HomeViewModelFactory() }
        bind() from provider { AddTaskViewModelFactory(instance()) }
        bind() from provider { MainScreenViewModelFactory() }
        bind() from provider { AddVocabViewModelFactory(instance()) }

    }

}