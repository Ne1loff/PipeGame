package com.example.pipegame.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pipegame.R
import com.example.pipegame.game.core.model.AbstractPipe
import com.example.pipegame.game.core.model.Direction
import com.example.pipegame.game.core.model.Pipe
import com.example.pipegame.game.core.model.PipeType
import com.example.pipegame.game.core.model.Position
import com.example.pipegame.ui.utils.conditional

@Composable
fun GameBoard(gameUiState: GameUiState, onRotate: (Position) -> Unit) {
    val level = gameUiState.currentLevel
    val size = level.boardSize

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rowModifier = Modifier
                .border(1.dp, Color.Blue)

            EdgeRow(rowModifier, size, level.startPosition, true)
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(1.dp, Color.Green),
            ) {
                for (i in 0..<size) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = rowModifier
                            .fillMaxWidth()
                            .aspectRatio(size.toFloat()),
                    ) {
                        for (j in 0..<size) {
                            val position = Position(j, i)
                            PipeCell(gameUiState.board[position.inArray(size)], size) {
                                onRotate(position)
                            }
                        }
                    }
                }
            }
            EdgeRow(rowModifier, size, level.finisPosition, false)
        }
    }
}


@Composable
fun EdgeRow(modifier: Modifier, size: Int, position: Position, isInput: Boolean) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(size.toFloat())
    ) {
        for (x in 0..<size) {
            val y = if (isInput) 0 else size - 1
            val currentPosition = Position(x, y)
            EdgeCell(currentPosition != position, isInput = isInput)
        }
    }
}

@Composable
fun EdgeCell(isEmpty: Boolean, isInput: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
    ) {
        if (!isEmpty) {
            val deg = if (isInput) Direction.TOP.toDegrees() else Direction.BOTTOM.toDegrees()
            GetImage(
                PipeType.EDGE_PIPE,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .border(1.dp, Color.Magenta)
                    .graphicsLayer(rotationZ = deg)
            )
        }
    }

}

@Composable
fun RowScope.PipeCell(pipe: Pipe?, n: Int, onRotate: () -> Unit) {
    val abstractPipe = pipe as? AbstractPipe
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f / n)
            .conditional(
                pipe?.rotatable ?: false,
                ifTrue = { clickable { onRotate() } },
                ifFalse = { background(Color.LightGray) }
            )
            .rotate(abstractPipe?.lookAt?.toDegrees() ?: 0f),
    ) {

        if (pipe != null) {
            val modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .border(1.dp, Color.Magenta)
            GetImage(pipe.type, modifier = modifier)
        }
    }
}

@Composable
fun GetImage(type: PipeType, modifier: Modifier) = when (type) {
    PipeType.STRAIGHT_PIPE -> Image(
        painterResource(R.drawable.straight_pipe),
        "",
        modifier = modifier.graphicsLayer(rotationZ = -90f)
    )

    PipeType.CORNER_PIPE -> Image(
        painterResource(R.drawable.corner_pipe),
        "",
        modifier = modifier.graphicsLayer(rotationZ = -90f)
    )

    PipeType.TEE_PIPE -> Image(
        painterResource(R.drawable.tee_pipe),
        "",
        modifier = modifier.graphicsLayer(rotationZ = -90f)
    )

    PipeType.CROSS_PIPE -> Image(
        painterResource(R.drawable.cross_pipe),
        "",
        modifier = modifier
    )

    PipeType.EDGE_PIPE -> Image(
        painter = painterResource(id = R.drawable.edge_pipe),
        contentDescription = "",
        modifier = modifier
    )
}

fun Direction.toDegrees(): Float = when (this) {
    Direction.TOP -> 0f
    Direction.LEFT -> 270f
    Direction.RIGHT -> 90f
    Direction.BOTTOM -> 180f
}