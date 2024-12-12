import utils.*

class PlotGrid(map: List<List<Char>>) : Grid<Char>(map) {
    fun getNeighbours(position: Position): List<Position> =
        getNeighbouringPositions(position).filter { isInBounds(it) }

    fun getNeighbouringPositions(position: Position): List<Position> {
        val offsets = listOf(
            Position(1, 0),
            Position(-1, 0),
            Position(0, 1),
            Position(0, -1)
        )
        return buildList {
            offsets.forEach { offset ->
                add(position + offset)
            }
        }
    }

    private fun getRegion(start: Position, visited: MutableSet<Position>): Region? {
        val symbol = map[start.x][start.y]
        val region = Region(symbol)

        fun backtrack(current: Position): Boolean {
            if (current in visited) return false
            if (!isInBounds(current)) return false
            if (map[current.x][current.y] != symbol) return false

            visited.add(current)
            region.add(current)

            getNeighbours(current).forEach { neighbor ->
                backtrack(neighbor)
            }

            return true
        }

        if (backtrack(start)) return region
        return null
    }

    fun findAllRegions(): List<Region> {
        val visited = mutableSetOf<Position>()
        val result = mutableListOf<Region>()

        map.forEachIndexed { x, row ->
            row.indices.forEach { y ->
                val position = Position(x, y)
                if (position !in visited) {
                    getRegion(position, visited)?.let { region ->
                        result.add(region)
                    }
                }
            }
        }

        return result
    }

    private fun getFences(region: Region): List<Position> = buildList {
        region.plots.forEach { plot ->
            val foreignRegions = this@PlotGrid.getNeighbouringPositions(plot)
                .filter {
                    if (!isInBounds(it)) true
                    else this@PlotGrid.get(it) != region.symbol
                }
            add(foreignRegions)
        }

    }.flatten()

    data class Point(val x: Float, val y: Float)

    fun getNumberOfSides(region: Region): Int {
        var points = region.plots.flatMap { pos ->
            listOf(
                Point(pos.x - 0.5f, pos.y - 0.5f),
                Point(pos.x - 0.5f, pos.y + 0.5f),
                Point(pos.x + 0.5f, pos.y + 0.5f),
                Point(pos.x + 0.5f, pos.y - 0.5f)
            )
        }.toSet()
        var totalCorners = 0
        points.forEach { corner ->
            val cornerPositions = listOf(
                Position(
                    x = (corner.x - 0.5f).toInt(),
                    y = (corner.y - 0.5f).toInt()
                ),
                Position(
                    x = (corner.x - 0.5f).toInt(),
                    y = (corner.y + 0.5f).toInt()
                ),
                Position(
                    x = (corner.x + 0.5f).toInt(),
                    y = (corner.y - 0.5f).toInt()
                ),
                Position(
                    x = (corner.x + 0.5f).toInt(),
                    y = (corner.y + 0.5f).toInt()
                )
            )

            val markedPositions = cornerPositions.map { pos ->
                pos to region.plots.any { it == pos }
            }.toMap()

            val neighbors = markedPositions.count { it.value }

            totalCorners += when (neighbors) {
                0, 4 -> 0
                1, 3 -> 1
                2 -> {
                    if ((markedPositions[cornerPositions[2]] == true && markedPositions[cornerPositions[1]] == true) ||
                        (markedPositions[cornerPositions[3]] == true && markedPositions[cornerPositions[0]] == true)
                    ) {
                        2
                    } else {
                        0
                    }
                }

                else -> 0
            }
        }
        return totalCorners
    }
}

data class Region(
    val symbol: Char,
) {
    val plots = mutableListOf<Position>()

    val priceWithPerimeter: Int
        get() {
            return plots.size * perimeter
        }
    val priceWithSides: Int
        get() {
            return plots.size * perimeter
        }
    private val perimeter: Int
        get() {
            val positions = plots
            val byRows = positions.groupBy { it.y }
            val byCols = positions.groupBy { it.x }

            val horizontalPerimeter = byRows.values.sumOf { row ->
                val xs = row.map { it.x }.sorted()
                2 + xs.zipWithNext().count { (a, b) -> b - a > 1 } * 2
            }

            val verticalPerimeter = byCols.values.sumOf { col ->
                val ys = col.map { it.y }.sorted()
                2 + ys.zipWithNext().count { (a, b) -> b - a > 1 } * 2
            }

            return horizontalPerimeter + verticalPerimeter
        }

    fun add(plot: Position) = plots.add(plot)
}


fun main() {
    fun solvePartOne(grid: PlotGrid): Int =
        grid.findAllRegions().sumOf {
            it.priceWithPerimeter
        }

    fun solvePartTwo(grid: PlotGrid): Int =
        grid.findAllRegions()
            .sumOf {
                grid.getNumberOfSides(it) * it.plots.size
            }


    val lines = readInput("Day12").map { it.toList() }
    val grid = PlotGrid(lines)
    "---------- Part 1 ----------".println()
    solvePartOne(grid).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(grid).println()
}