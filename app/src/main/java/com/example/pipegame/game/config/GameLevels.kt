package com.example.pipegame.game.config

import com.example.pipegame.game.core.model.Direction.*
import com.example.pipegame.game.core.model.GameLevel
import com.example.pipegame.game.core.model.PipeMeta
import com.example.pipegame.game.core.model.PipeMeta.Companion.straight
import com.example.pipegame.game.core.model.PipeMeta.Companion.tee
import com.example.pipegame.game.core.model.PipeMeta.Companion.corner
import com.example.pipegame.game.core.model.Position

// @formatter:off
val LVL_1_MAP: Array<Array<PipeMeta?>> = arrayOf(
    arrayOf(corner(),           straight(), tee(),                      corner(), null),
    arrayOf(straight(RIGHT),    null,       straight(active = false),   null,     null),
    arrayOf(tee(),              straight(), corner(),                   null,     null),
    arrayOf(straight(LEFT),     null,       null,                       null,     null),
    arrayOf(straight(),         null,       null,                       null,     null),
)
// @formatter:on
val LVL_1: GameLevel = GameLevel(
    index = 1,
    size = 5,
    input = Position(3, 0),
    output = Position(0, 4),
    maxSteps = 30,
    map = toMap(LVL_1_MAP)
)

fun toMap(map: Array<Array<PipeMeta?>>): Array<PipeMeta?> =
    map.flatMap { it.asSequence() }.toTypedArray()