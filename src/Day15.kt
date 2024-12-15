import utils.Position
import utils.println
import utils.readInput
import utils.set
import utils.get
import java.util.ArrayDeque

enum class RobotDirection(val positionOffset: Position) {
    North(Position(-1, 0)),
    South(Position(1, 0)),
    East(Position(0, 1)),
    West(Position(0, -1));

    companion object {
        fun getDirection(char: Char): RobotDirection {
            return when (char) {
                '^' -> North
                'V', 'v' -> South
                '>' -> East
                '<' -> West
                else -> throw IllegalArgumentException("Invalid direction character: $char")
            }
        }
    }

}

fun main() {
    fun MutableList<MutableList<Char>>.getGPSCoordsTotal(target: Char): Long =
        mapIndexed { row, r ->
            r.mapIndexed { col, char ->
                if (char == target) row * 100L + col else 0L
            }.sum()
        }.sum()

    fun MutableList<MutableList<Char>>.getInitialPosition(): Position = indices
        .firstNotNullOfOrNull { row ->
            this[row].indices.firstNotNullOfOrNull { col ->
                if (this[row][col] == '@') Position(row, col) else null
            }
        } ?: Position(0, 0)

    fun MutableList<MutableList<Char>>.handleFreeSpace(position: Position, direction: RobotDirection) {
        this.set(position, '.')
        this.set(position + direction.positionOffset, '@')
    }

    fun MutableList<MutableList<Char>>.handleBox(position: Position, direction: RobotDirection): Boolean {
        var boxCount = 0
        var tempPosition = position + direction.positionOffset
        while (this.get(tempPosition) == 'O') {
            boxCount++
            tempPosition += direction.positionOffset
        }
        if (this.get(tempPosition) == '.') {
            this.set(position, '.')
            this.set(position + direction.positionOffset, '@')
            this.set(tempPosition, 'O')
            return true
        }
        return false
    }

    fun MutableList<MutableList<Char>>.handleBigBox(
        leftPosition: Position,
        rightPosition: Position,
        direction: RobotDirection
    ): Boolean {
        val boxesToPush = mutableListOf(leftPosition to '[', rightPosition to ']')
        fun bfs(): Boolean {
            val queue = ArrayDeque<Position>()
            queue.add(leftPosition)
            queue.add(rightPosition)
            while (queue.isNotEmpty()) {
                val position: Position = queue.removeFirst()
                val nextPosition = position + direction.positionOffset

                if (nextPosition in boxesToPush.map { it.component1() }) continue
                when (this.get(nextPosition)) {
                    '#' -> return false
                    '[' -> {
                        queue.add(nextPosition)
                        queue.add(nextPosition + RobotDirection.East.positionOffset)
                        boxesToPush.add(nextPosition to '[')
                        boxesToPush.add(nextPosition + RobotDirection.East.positionOffset to ']')
                    }

                    ']' -> {
                        queue.add(nextPosition)
                        queue.add(nextPosition + RobotDirection.West.positionOffset)
                        boxesToPush.add(nextPosition to ']')
                        boxesToPush.add(nextPosition + RobotDirection.West.positionOffset to '[')
                    }
                }
            }
            return true
        }

        if (!bfs()) return false
        boxesToPush.forEach { (position, _) -> this.set(position, '.') }
        boxesToPush.forEach { (position, value) -> this.set(position + direction.positionOffset, value) }
        return true
    }

    fun solvePartOne(input: List<String>): Long {
        val map = input.takeWhile { it != "" }.map { it.toMutableList() }.toMutableList()
        var movements = input.drop(map.size + 1).joinToString("")
        var position = map.getInitialPosition()

            while (movements.isNotEmpty()) {
                val direction = RobotDirection.getDirection(movements.first())
                movements = movements.drop(1)

                val nextPosition = position + direction.positionOffset
                when (map.get(nextPosition)) {
                    '.' -> map.handleFreeSpace(position, direction).also { position = nextPosition }
                    'O' -> if (map.handleBox(position, direction)) position = nextPosition
                }
            }
        return map.getGPSCoordsTotal('O')
    }

    fun solvePartTwo(input: List<String>): Long {
        val map = input.asSequence().takeWhile { it != "" }.map { it.toMutableList() }
            .takeWhile { it.isNotEmpty() }
            .map { row ->
                row.flatMap { char ->
                    when (char) {
                        '#' -> listOf('#', '#')
                        'O' -> listOf('[', ']')
                        '.' -> listOf('.', '.')
                        '@' -> listOf('@', '.')
                        else -> listOf()
                    }
                }.toMutableList()
            }.toMutableList()
        var movmenets = input.drop(map.size + 1).joinToString("")

        var position = map.getInitialPosition()

        while (movmenets.isNotEmpty()) {
            val direction = RobotDirection.getDirection(movmenets.first())
            movmenets = movmenets.drop(1)

            val nextPosition = position + direction.positionOffset
            when (map.get(nextPosition)) {
                '.' -> map.handleFreeSpace(position, direction).also { position = nextPosition }
                '[' -> if (map.handleBigBox
                        (
                        leftPosition = nextPosition,
                        rightPosition = nextPosition + RobotDirection.East.positionOffset,
                        direction
                    )
                ) {
                    map.set(position, '.')
                    map.set(position + direction.positionOffset, '@')
                    position = nextPosition
                }

                ']' -> if (map.handleBigBox
                        (
                        leftPosition = nextPosition + RobotDirection.West.positionOffset,
                        rightPosition = nextPosition,
                        direction
                    )
                ) {
                    map.set(position, '.')
                    map.set(position + direction.positionOffset, '@')
                    position = nextPosition
                }
            }
        }

        return map.getGPSCoordsTotal('[')
    }

    val lines: List<String> = readInput("Day15")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}