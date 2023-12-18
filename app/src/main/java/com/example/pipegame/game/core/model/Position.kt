package com.example.pipegame.game.core.model

data class Position(val x: Int, val y: Int) {

    companion object {
        fun fromArray(index: Int, size: Int): Position = Position(index % size, index / size)
    }

    fun inArray(n: Int): Int = y * n + x

    fun topIndex(size: Int): Int? {
        val next = y - 1
        return if (next >= 0) next * size + x else null
    }

    fun rightIndex(size: Int): Int? {
        val next = x + 1
        return if (next < size) y * size + next else null
    }

    fun bottomIndex(size: Int): Int? {
        val next = y + 1
        return if (next < size) next * size + x else null
    }

    fun leftIndex(size: Int): Int? {
        val next = x - 1
        return if (next >= 0) y * size + next else null
    }
}