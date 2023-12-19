package com.example.pipegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pipegame.game.core.GameState
import com.example.pipegame.ui.GameBoard
import com.example.pipegame.ui.GameViewModel
import com.example.pipegame.ui.theme.PipeGameTheme
import com.example.pipegame.utils.conditional


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as PipeGameApplication).container
        setContent {
            PipeGameTheme {
                val gameViewModel: GameViewModel =
                    viewModel(factory = GameViewModel.provideFactory(appContainer.gameManager))
                val uiState by gameViewModel.uiState.collectAsState()

                GameResultAlert(
                    show = when (uiState.gameState) {
                        GameState.RESULT_LOSE, GameState.RESULT_WIN -> true; else -> false
                    },
                    isWin = uiState.gameState == GameState.RESULT_WIN,
                    onConfirmation = {
                        gameViewModel.restart()
                    },
                    onDismissRequest = {
                        gameViewModel.restart()
                    }
                )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    GameBoard(
                        gameUiState = uiState,
                        onRotate = gameViewModel::rotate,
                        onRestart = gameViewModel::restart,
                        onLevelChange = gameViewModel::changeLevel,
                        onComplexityChange = gameViewModel::setComplexity,
                    )
                }
            }
        }
    }
}

@Composable
fun GameResultAlert(
    show: Boolean,
    onConfirmation: (Boolean) -> Unit,
    onDismissRequest: (Boolean) -> Unit,
    isWin: Boolean
) {
    if (show) {
        AlertDialog(
            title = {
                Text(conditional(isWin, "Победа!", "Вы проиграли."))
            },
            text = {
                Text(
                    conditional(
                        isWin,
                        "Поздравляю вы справились с испытанием",
                        "К сожалению вы не смогли справиться с испытанием за отведенное количество шагов..."
                    )
                )
            },
            onDismissRequest = { onDismissRequest(isWin) },
            confirmButton = {
                TextButton(onClick = { onConfirmation(isWin) }) {
                    Text(conditional(isWin, "Следующий раунд", "Попробовать снова"))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest(isWin) }) {
                    Text(conditional(isWin, "Попробовать снова", "Выйти"))
                }
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PipeGameTheme {
        Greeting("Android")
    }
}