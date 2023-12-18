package com.example.pipegame.game.core.model

import kotlin.math.roundToInt

enum class GameComplexity(private val ratio: Float) {
    EASY(1.3f),
    MEDIUM(1.15f),
    HARD(1f);

    fun evaluate(steps: Int): Int = (steps * ratio).roundToInt()
}

data class GameLevel(val index: Int, val size: Int, val input: Position, val output: Position, val map: Array<PipeMeta?>, val maxSteps: Int)

data class Settings(var complexity: GameComplexity, var level: GameLevel)