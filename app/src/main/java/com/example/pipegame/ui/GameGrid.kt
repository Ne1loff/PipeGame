package com.example.pipegame.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pipegame.R
import com.example.pipegame.game.config.LEVELS
import com.example.pipegame.game.core.GameState
import com.example.pipegame.game.core.model.AbstractPipe
import com.example.pipegame.game.core.model.Direction
import com.example.pipegame.game.core.model.GameComplexity
import com.example.pipegame.game.core.model.GameLevel
import com.example.pipegame.game.core.model.Pipe
import com.example.pipegame.game.core.model.PipeType
import com.example.pipegame.game.core.model.Position
import com.example.pipegame.ui.utils.conditional

@Composable
fun GameBoard(
    gameUiState: GameUiState,
    onRotate: (Position) -> Unit,
    onRestart: () -> Unit,
    onLevelChange: (GameLevel) -> Unit,
    onComplexityChange: (GameComplexity) -> Unit,
) {
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

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val modifier = Modifier.width(150.dp)
                SettingsSelector(
                    label = "Уровень",
                    initialSelectIndex = 0,
                    map = LEVELS.associateBy({ "${it.index}" }, { it }),
                    onChange = { onLevelChange(it) },
                    enabled = gameUiState.gameState != GameState.IN_PROGRESS,
                    modifier = modifier
                )
                SettingsSelector(
                    label = "Сложность",
                    initialSelectIndex = 0,
                    map = GameComplexity.entries
                        .associateBy({ it.name.toLowerCase(Locale.current) }, { it }),
                    onChange = { onComplexityChange(it) },
                    enabled = gameUiState.gameState != GameState.IN_PROGRESS,
                    modifier = modifier
                )
                Text(
                    text = "${gameUiState.remainingSteps} / ${gameUiState.maxSteps}",
                    fontSize = 18.sp
                )
            }
            EdgeRow(rowModifier, size, level.startPosition, true)
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
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
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                FloatingActionButton(onClick = onRestart) {
                    Icon(Icons.Filled.Refresh, "")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingsSelector(
    label: String,
    initialSelectIndex: Int,
    map: Map<String, T>,
    onChange: (T) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val levelNames = map.keys.toList()
    var selectedOptionText by remember { mutableStateOf(levelNames[initialSelectIndex]) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier.menuAnchor(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            levelNames.forEach { selectionOption ->
                DropdownMenuItem(
                    enabled = enabled,
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onChange(map[selectionOption]!!)
                    },
                    text = {
                        Text(text = selectionOption)
                    }
                )
            }
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