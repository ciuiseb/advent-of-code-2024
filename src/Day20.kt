import utils.*
import kotlin.math.abs

class RaceTrack(map: MutableList<MutableList<Char>>) : Grid<Char>(map) {
    private val start = getPositionOf('S')
    private val pathBlocks by lazy {
        sortedPath().withIndex().associate { (index, position) -> position to index }
    }

    fun getPotentialCheats(): List<Position> = buildList {
        map.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, char ->
                val pos = Position(rowIndex, columnIndex)
                if (char == '#' && !isBorder(pos) && getNeighbours(pos).count { get(it) != '#' } > 1) {
                    add(pos)
                }
            }
        }
    }

    fun cheat(position: Position): Int {
        val neighbors = getNeighbours(position).filter { get(it) != '#' }

        for (a in neighbors) {
            for (b in neighbors) {
                if (a != b && (a.x == b.x || a.y == b.y)) {
                    return abs(pathBlocks[a]!! - pathBlocks[b]!!) - 2
                }
            }
        }
        return -1
    }

    fun cheatForLonger(): Int {
        var count = 0
        for ((pos1, dist1) in pathBlocks) {
            for ((pos2, dist2) in pathBlocks) {
                val manhattan = abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y)
                if (manhattan <= 20 && abs(dist2 - dist1) - manhattan >= 100) count++
            }
        }
        return count / 2
    }

    private fun sortedPath(): List<Position> {
        val ordered = mutableListOf<Position>()
        val visited = mutableSetOf<Position>()
        var current = start

        while (current !in visited) {
            ordered.add(current)
            visited.add(current)
            current = getNeighbours(current)
                .filter { get(it) != '#' }
                .firstOrNull { it !in visited } ?: break
        }
        return ordered
    }

    private fun getPositionOf(target: Char): Position {
        for (x in map.indices) {
            for (y in map[0].indices) {
                if (get(Position(x, y)) == target) return Position(x, y)
            }
        }
        error("")
    }

    private fun isBorder(pos: Position) = pos.x in listOf(0, map.size) || pos.y in listOf(0, map[0].size)

    private fun getNeighbours(position: Position) = buildList {
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        for ((dx, dy) in directions) {
            val newPos = Position(position.x + dx, position.y + dy)
            if (isInBounds(newPos)) add(newPos)
        }
    }
}

fun main() {
    fun solvePartOne(input: List<String>): Int {
        val track = RaceTrack(input.map { it.toMutableList() }.toMutableList())
        return track.getPotentialCheats()
            .map { pos -> track.cheat(pos) }
            .count { it >= 100 }
    }


    fun solvePartTwo(input: List<String>): Int {
        val track = RaceTrack(input.map { it.toMutableList() }.toMutableList())
        return track.cheatForLonger()
    }

    val lines = readInput("Day20")
    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}