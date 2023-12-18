package com.example.pipegame.data

import android.content.Context
import com.example.pipegame.game.config.LVL_1
import com.example.pipegame.game.core.GameManager
import com.example.pipegame.game.core.model.GameComplexity
import com.example.pipegame.game.core.model.Settings

interface AppContainer {
    val gameManager: GameManager
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val gameManager: GameManager by lazy {
        val settings = Settings(GameComplexity.EASY, LVL_1)
        val manager = GameManager.init(settings)
        manager.reconfigure(settings)
        manager
    }
}