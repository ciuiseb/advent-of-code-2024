import utils.*

enum class TrailDirections(val position: Position) {
    North(Position(0, -1)),
    South(Position(0, 1)),
    East(Position(1, 0)),
    West(Position(-1, 0))
}

fun main() {
    fun Grid.getNeighboursGreaterWithOne(value: Int, position: Position): List<Position> =
        getNeighbours(position).filter { neighbourPosition ->
            map[neighbourPosition.x][neighbourPosition.y] == value + 1
        }

    fun Grid.backtrack(position: Position): List<Position> {
        val currentValue = map[position.x][position.y]
        val neighbours = getNeighboursGreaterWithOne(currentValue, position)

        if (neighbours.isEmpty()) return if (currentValue == 9) listOf(position) else emptyList()

        return neighbours.flatMap { neighbour ->
            backtrack(neighbour)
        }
    }

    fun Grid.getTailsForTrailHead(position: Position): Long = backtrack(position).toSet().size.toLong()


    fun Grid.getTrailsForTrailHead(position: Position): Long = backtrack(position).size.toLong()


    fun Grid.getPotentialTrailHeads(): List<Position> = buildList {
        this@getPotentialTrailHeads.map.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, number ->
                if (number == 0)
                    add(Position(rowIndex, columnIndex))
            }
        }
    }

    fun solvePartOne(grid: Grid): Long = grid.getPotentialTrailHeads()
        .sumOf { grid.getTailsForTrailHead(it) }


    fun solvePartTwo(grid: Grid): Long = grid.getPotentialTrailHeads()
        .sumOf { grid.getTrailsForTrailHead(it) }

    val lines: List<String> = readInput("Day10")
    val map = lines.map { line -> line.map { it.digitToInt() } }
    val grid = Grid(map)

    "---------- Part 1 ----------".println()
    solvePartOne(grid).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(grid).println()
}