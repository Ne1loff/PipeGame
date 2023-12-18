package com.example.pipegame.game.core.model

enum class PipeType {
    STRAIGHT_PIPE,
    CORNER_PIPE,
    TEE_PIPE,
    CROSS_PIPE,
    EDGE_PIPE
}

sealed interface Pipe {
    val type: PipeType
    var rotatable: Boolean
    val connectionCount: Int

    fun getConnectedPipe(dir: Direction): Pipe?
    fun updateConnection(pipe: Pipe?, direction: Direction)
    fun rotate()
}

class PipeMeta(
    val type: PipeType,
    val direction: Direction,
    val active: Boolean
) {
    companion object {
        fun straight(startDirection: Direction = Direction.TOP, active: Boolean = true): PipeMeta =
            PipeMeta(PipeType.STRAIGHT_PIPE, startDirection, active)

        fun corner(startDirection: Direction = Direction.TOP, active: Boolean = true): PipeMeta =
            PipeMeta(PipeType.CORNER_PIPE, startDirection, active)

        fun tee(startDirection: Direction = Direction.TOP, active: Boolean = true): PipeMeta =
            PipeMeta(PipeType.TEE_PIPE, startDirection, active)

        fun cross(startDirection: Direction = Direction.TOP, active: Boolean = true): PipeMeta =
            PipeMeta(PipeType.CROSS_PIPE, startDirection, active)
    }

    fun createPipe() = when (type) {
        PipeType.STRAIGHT_PIPE -> StraightPipe().apply {
            rotatable = active; lookAt = direction
        }

        PipeType.CORNER_PIPE -> CornerPipe().apply {
            rotatable = active; lookAt = direction
        }

        PipeType.TEE_PIPE -> TeePipe().apply {
            rotatable = active; lookAt = direction
        }

        PipeType.CROSS_PIPE -> CrossPipe().apply {
            rotatable = active; lookAt = direction
        }

        else -> throw IllegalStateException()

    }
}

abstract class AbstractPipe : Pipe {
    var lookAt: Direction = Direction.TOP
    override var rotatable: Boolean = true

    override fun rotate() {
        if (rotatable) {
            lookAt = lookAt.next()
        }
    }

    override fun updateConnection(pipe: Pipe?, direction: Direction) {
        if (pipe == null) {
            disconnect(direction)
        } else {
            connect(pipe, direction)
        }
    }

    protected fun relativeRotation(direction: Direction): Direction = when (lookAt) {
        Direction.TOP -> direction
        Direction.RIGHT -> direction.prev()
        Direction.BOTTOM -> direction.opposite()
        Direction.LEFT -> direction.next()
    }

    private fun connect(pipe: Pipe, direction: Direction) {
        val first = this
        val second = pipe as AbstractPipe
        val secondDirection = direction.opposite()

        if (isDisconnected(first, second, direction)
                .and(first.canConnect(direction))
                .and(second.canConnect(secondDirection))
        ) {
            first.setConnection(second, direction)
            second.setConnection(first, secondDirection)
        }
    }

    private fun disconnect(direction: Direction) {
        val firstDirection = direction
        val secondDirection = firstDirection.opposite()

        val second = (this.getConnectedPipe(firstDirection) ?: return) as AbstractPipe

        if (isConnected(this, second, firstDirection)) {
            this.setConnection(null, firstDirection)
            second.setConnection(null, secondDirection)
        }
    }

    private fun isDisconnected(first: Pipe, second: Pipe, direction: Direction): Boolean {
        val connectedPipe = first.getConnectedPipe(direction)
        return second != connectedPipe
    }

    private fun isConnected(first: Pipe, second: Pipe, direction: Direction): Boolean {
        val connectedPipe = first.getConnectedPipe(direction)
        return second == connectedPipe
    }

    abstract fun setConnection(pipe: Pipe?, direction: Direction)
    abstract fun canConnect(direction: Direction): Boolean
}

class EdgePipe(private var connection: Pipe? = null, val position: Position) : AbstractPipe() {

    override val type: PipeType = PipeType.EDGE_PIPE
    override var rotatable: Boolean = false
    override val connectionCount: Int = 1

    override fun getConnectedPipe(dir: Direction): Pipe? = connection

    override fun rotate() {}

    override fun canConnect(direction: Direction): Boolean = true

    override fun setConnection(pipe: Pipe?, direction: Direction) {
        connection = pipe
    }

}

open class StraightPipe(protected var top: Pipe? = null, protected var bottom: Pipe? = null) :
    AbstractPipe() {

    override val connectionCount: Int = 2
    override val type: PipeType = PipeType.STRAIGHT_PIPE

    override fun getConnectedPipe(dir: Direction): Pipe? = when (relativeRotation(dir)) {
        Direction.TOP -> top
        Direction.BOTTOM -> bottom
        else -> null
    }

    override fun setConnection(pipe: Pipe?, direction: Direction) {
        when (relativeRotation(direction)) {
            Direction.TOP -> top = pipe
            Direction.BOTTOM -> bottom = pipe
            else -> {}
        }
    }

    override fun canConnect(direction: Direction): Boolean = when (relativeRotation(direction)) {
        Direction.TOP, Direction.BOTTOM -> true
        else -> false
    }
}

open class CornerPipe(private var top: Pipe? = null, private var right: Pipe? = null) :
    AbstractPipe() {

    override val connectionCount: Int = 2
    override val type: PipeType = PipeType.CORNER_PIPE

    override fun getConnectedPipe(dir: Direction): Pipe? = when (relativeRotation(dir)) {
        Direction.TOP -> top
        Direction.RIGHT -> right
        else -> null
    }

    override fun setConnection(pipe: Pipe?, direction: Direction) {
        when (relativeRotation(direction)) {
            Direction.TOP -> top = pipe
            Direction.RIGHT -> right = pipe
            else -> {}
        }
    }

    override fun canConnect(direction: Direction): Boolean = when (relativeRotation(direction)) {
        Direction.TOP, Direction.RIGHT -> true
        else -> false
    }
}

open class TeePipe(top: Pipe? = null, bottom: Pipe? = null, protected var right: Pipe? = null) :
    StraightPipe(top, bottom) {

    override val connectionCount: Int = 3
    override val type: PipeType = PipeType.TEE_PIPE

    override fun getConnectedPipe(dir: Direction): Pipe? = when (relativeRotation(dir)) {
        Direction.TOP -> top
        Direction.RIGHT -> right
        Direction.BOTTOM -> bottom
        else -> null
    }

    override fun setConnection(pipe: Pipe?, direction: Direction) {
        when (relativeRotation(direction)) {
            Direction.TOP -> top = pipe
            Direction.RIGHT -> right = pipe
            Direction.BOTTOM -> bottom = pipe
            else -> {}
        }
    }

    override fun canConnect(direction: Direction): Boolean = when (relativeRotation(direction)) {
        Direction.TOP, Direction.RIGHT, Direction.BOTTOM -> true
        else -> false
    }
}

open class CrossPipe(
    top: Pipe? = null,
    bottom: Pipe? = null,
    right: Pipe? = null,
    private var left: Pipe? = null
) : TeePipe(top, bottom, right) {

    override val connectionCount: Int = 4
    override val type: PipeType = PipeType.CROSS_PIPE

    override var rotatable: Boolean = false
        set(_) {
            field = false
        }

    override fun getConnectedPipe(dir: Direction): Pipe? = when (relativeRotation(dir)) {
        Direction.TOP -> top
        Direction.RIGHT -> right
        Direction.BOTTOM -> bottom
        Direction.LEFT -> left
    }

    override fun setConnection(pipe: Pipe?, direction: Direction) {
        when (relativeRotation(direction)) {
            Direction.TOP -> top = pipe
            Direction.RIGHT -> right = pipe
            Direction.BOTTOM -> bottom = pipe
            Direction.LEFT -> left = pipe
        }
    }

    override fun canConnect(direction: Direction): Boolean = true
}