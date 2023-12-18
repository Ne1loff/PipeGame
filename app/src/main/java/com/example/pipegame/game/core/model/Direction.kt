package com.example.pipegame.game.core.model

enum class Direction {
    TOP,
    LEFT,
    RIGHT,
    BOTTOM;

    fun next() = when (this) {
        TOP -> RIGHT
        RIGHT -> BOTTOM
        BOTTOM -> LEFT
        LEFT -> TOP
    }

    fun prev() = when (this) {
        TOP -> LEFT
        LEFT -> BOTTOM
        BOTTOM -> RIGHT
        RIGHT -> TOP
    }

    fun opposite() = when (this) {
        TOP -> BOTTOM
        LEFT -> RIGHT
        BOTTOM -> TOP
        RIGHT -> LEFT
    }
}