import utils.*
import java.util.PriorityQueue
class MemoryGrid(size: Int) : Grid<Boolean>(MutableList(size) { MutableList(size) { false } }) {
    fun markCorrupted(position: Position) {
        if (isInBounds(position)) {
            map[position.x][position.y] = true
        }
    }

    private fun isCorrupted(position: Position): Boolean =
        if (isInBounds(position)) get(position) else true

    fun getNeighbours(position: Position): List<Position> {
        return buildList {
            val directions = listOf(
                0 to 1,
                1 to 0,
                0 to -1,
                -1 to 0
            )

            for ((dx, dy) in directions) {
                val newPos = Position(position.x + dx, position.y + dy)
                if (isInBounds(newPos) && !isCorrupted(newPos)) {
                    add(newPos)
                }
            }
        }
    }
}

fun dijkstra(grid: MemoryGrid, start: Position, end: Position): Int? {
    val distances = mutableMapOf<Position, Int>()
    val toVisit = PriorityQueue<Pair<Position, Int>>(compareBy { it.second })

    distances[start] = 0
    toVisit.offer(start to 0)

    while (toVisit.isNotEmpty()) {
        val (current, distance) = toVisit.poll()

        if (current == end) {
            return distance
        }

        if (distance > distances.getOrDefault(current, Int.MAX_VALUE)) {
            continue
        }

        for (next in grid.getNeighbours(current)) {
            val newDistance = distance + 1
            if (newDistance < distances.getOrDefault(next, Int.MAX_VALUE)) {
                distances[next] = newDistance
                toVisit.offer(next to newDistance)
            }
        }
    }

    return null
}

fun main() {
    fun solvePartOne(input: List<String>): Int {
        val grid = MemoryGrid(71)

        input.take(1024).forEach { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            grid.markCorrupted(Position(x, y))
        }

        return dijkstra(grid, Position(0, 0), Position(70, 70)) ?: -1
    }

    fun solvePartTwo(input: List<String>): Position {
        var left = 12
        var right = input.size
        var answer = right

        val bytePositions = input.map { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            Position(x, y)
        }
        while (left <= right) {
            val mid = (left + right) / 2
            val grid = MemoryGrid(71)

            bytePositions.take(mid).forEach { (x, y) ->
                grid.markCorrupted(Position(x, y))
            }

            val path = dijkstra(grid, Position(0, 0), Position(70, 70))

            if (path == null) {
                answer = mid
                right = mid - 1
            } else {
                left = mid + 1
            }
        }

        return bytePositions[answer - 1]
    }

    val lines: List<String> = readInput("Day18")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}