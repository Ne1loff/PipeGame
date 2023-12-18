package com.example.pipegame.game.core

import androidx.compose.runtime.Immutable
import com.example.pipegame.game.core.model.Direction
import com.example.pipegame.game.core.model.EdgePipe
import com.example.pipegame.game.core.model.GameLevel
import com.example.pipegame.game.core.model.Pipe
import com.example.pipegame.game.core.model.Position
import com.example.pipegame.game.core.model.Settings
import java.util.Collections

@Immutable
class GameManager private constructor(var gameSettings: Settings) : GameResultNotifier {

    companion object {
        private var instance: GameManager? = null

        fun init(gameSettings: Settings): GameManager {
            if (instance == null) {
                instance = GameManager(gameSettings)
            }

            return instance as GameManager
        }
    }

    private var gameState = GameState.IDLE

    private val listeners: MutableList<GameResultListener> = ArrayList()

    private var boardSize: Int = 0

    private var roundSteps: Int = 0
    private var remainingSteps: Int = 0

    private var start: EdgePipe = EdgePipe(position = Position(0, 0))
    private var finish: EdgePipe = EdgePipe(position = Position(0, 0))

    private var connectionsBoard: Array<Pipe?> = emptyArray()

    private fun configure() {
        val level = gameSettings.level
        val complexity = gameSettings.complexity

        this.boardSize = level.size

        this.roundSteps = complexity.evaluate(level.maxSteps)
        this.remainingSteps = this.roundSteps

        this.connectionsBoard = createBoard(level)

        this.start = EdgePipe(position = level.input)
        this.finish = EdgePipe(position = level.output)

        updateAllConnections()
    }

    private fun createBoard(level: GameLevel): Array<Pipe?> = Array(level.size * level.size) {
        val meta = level.map[it] ?: return@Array null
        meta.createPipe()

    }

    override fun getListeners(): List<GameResultListener> = Collections.unmodifiableList(listeners)

    fun restart() {
        gameState = GameState.IDLE
        remainingSteps = roundSteps
        val level = gameSettings.level

        for (index in connectionsBoard.indices) {
            val meta = level.map[index]
            connectionsBoard[index] = meta?.createPipe()
        }

        start.setConnection(null, Direction.TOP)
        finish.setConnection(null, Direction.TOP)

        updateAllConnections()
    }

    fun reconfigure(settings: Settings) {
        if (gameState == GameState.IN_PROGRESS) return

        this.gameSettings = settings
        configure()
    }


    fun board() = connectionsBoard.toList()
    fun getRoundSteps() = roundSteps
    fun getRemainingSteps() = remainingSteps
    fun getGameState() = gameState

    fun rotate(position: Position) {
        gameState = GameState.IN_PROGRESS

        if ((remainingSteps--) <= 0) {
            lose()
            return
        }

        connectionsBoard[position.inArray(boardSize)]?.rotate()
        updateConnections(position)
        checkConnection()
    }

    private fun win() {
        gameState = GameState.RESULT_WIN
        notifyWin()
    }

    private fun lose() {
        gameState = GameState.RESULT_LOSE
        notifyLose()
    }

    private fun updateAllConnections() {
        for (i in connectionsBoard.indices) {
            val position = Position.fromArray(i, boardSize)
            updateConnections(position)
        }
    }

    private fun updateConnections(position: Position) {
        val pipe = connectionsBoard[position.inArray(boardSize)] ?: return

        val isStartPosition = start.position == position
        val isFinishPosition = finish.position == position

        val topPipe = if (isStartPosition) start
        else position.topIndex(boardSize)?.let { connectionsBoard[it] }
        val bottomPipe = if (isFinishPosition) finish
        else position.bottomIndex(boardSize)?.let { connectionsBoard[it] }

        val rightPipe = position.rightIndex(boardSize)?.let { connectionsBoard[it] }
        val leftPipe = position.leftIndex(boardSize)?.let { connectionsBoard[it] }

        pipe.updateConnection(topPipe, Direction.TOP)
        pipe.updateConnection(rightPipe, Direction.RIGHT)
        pipe.updateConnection(bottomPipe, Direction.BOTTOM)
        pipe.updateConnection(leftPipe, Direction.LEFT)
    }

    private fun checkConnection() {
        val firstPipe: Pipe = start.getConnectedPipe(Direction.BOTTOM) ?: return
        val directions = Direction.entries

        val toVisitStack = ArrayDeque<Pipe>().apply { addLast(firstPipe) }
        val visitedPipes = HashSet<Pipe>().apply { add(start) }

        var current: Pipe

        do {
            current = toVisitStack.removeLast()
            visitedPipes.add(current)

            val maxEmptyConnections = (directions.size - current.connectionCount) + 1
            var emptyConnectionCount = 0
            for (direction in directions) {
                val pipe = current.getConnectedPipe(direction)

                if (pipe == null) {
                    when (++emptyConnectionCount) {
                        maxEmptyConnections -> return
                        else -> continue
                    }
                }

                if (!visitedPipes.contains(pipe)) {
                    toVisitStack.addLast(pipe)
                }
            }
        } while (toVisitStack.isNotEmpty())

        if (visitedPipes.contains(finish)) {
            win()
        }
    }


}