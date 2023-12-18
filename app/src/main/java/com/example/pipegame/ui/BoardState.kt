package com.example.pipegame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pipegame.game.core.GameManager
import com.example.pipegame.game.core.GameState
import com.example.pipegame.game.core.model.GameComplexity
import com.example.pipegame.game.core.model.Pipe
import com.example.pipegame.game.core.model.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/*data class GameListener(
    val onWinDo: () -> Unit,
    val onLoseDo: () -> Unit
) : GameResultListener {
    override fun onWin() = onWinDo()

    override fun onLose() = onLoseDo()

}*/

data class LevelInfo(
    val index: Int,
    val boardSize: Int,
    val startPosition: Position,
    val finisPosition: Position,
)

data class GameUiState(
    val currentLevel: LevelInfo,
    val currentComplexity: GameComplexity,
    val maxSteps: Int,
    val remainingSteps: Int,
    val gameState: GameState,
    val board: List<Pipe?>
)

private data class GameViewModelState(
    val currentLevel: LevelInfo = LevelInfo(1, 5, Position(0, 0), Position(0, 0)),
    val currentComplexity: GameComplexity = GameComplexity.MEDIUM,
    val maxSteps: Int = 0,
    val remainingSteps: Int = maxSteps,
    val gameState: GameState = GameState.IDLE,
    val board: List<Pipe?> = ArrayList()
) {
    fun toUiState() = GameUiState(
        currentLevel = currentLevel,
        currentComplexity = currentComplexity,
        maxSteps = maxSteps,
        remainingSteps = remainingSteps,
        gameState = gameState,
        board = board
    )
}

class GameViewModel(private val gameManager: GameManager) : ViewModel() {

    private val viewModelState = MutableStateFlow(GameViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        setupState()
    }

    private fun setupState() {
        viewModelState.update {
            val settings = gameManager.gameSettings
            val level = settings.level

            it.copy(
                currentLevel = it.currentLevel.copy(
                    index = level.index,
                    boardSize = level.size,
                    startPosition = level.input,
                    finisPosition = level.output
                ),
                currentComplexity = settings.complexity,
                maxSteps = level.maxSteps,
                board = gameManager.board(),
                gameState = gameManager.getGameState()
            )
        }
    }

    fun restart() {
        gameManager.restart()
        setupState()
    }

    fun rotate(position: Position) {
        gameManager.rotate(position)
        viewModelState.update {
            it.copy(
                remainingSteps = gameManager.getRemainingSteps(),
                gameState = gameManager.getGameState(),
                board = gameManager.board(),
            )
        }
    }

    fun setComplexity(complexity: GameComplexity) {
        gameManager.reconfigure(gameManager.gameSettings.copy(complexity = complexity))
        viewModelState.update {
            it.copy(currentComplexity = gameManager.gameSettings.complexity)
        }
    }

    companion object {
        fun provideFactory(
            manager: GameManager,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(manager) as T
            }
        }
    }
}