package com.example.pipegame.game.core

interface GameResultNotifier {

    fun getListeners(): List<GameResultListener>
    fun notifyWin() = getListeners().forEach(GameResultListener::onWin)
    fun notifyLose() = getListeners().forEach(GameResultListener::onLose)

}

interface GameResultListener {
    fun onWin()
    fun onLose()
}