package com.example.pipegame

import android.app.Application
import com.example.pipegame.data.AppContainer
import com.example.pipegame.data.AppContainerImpl

class PipeGameApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}