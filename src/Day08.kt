import utils.*

data class Roof(
    var map: MutableList<MutableList<Char>>
) {
    private val antenas by lazy {
        map.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { columnIndex, char ->
                if (char == '.') null else char to Position(rowIndex, columnIndex)
            }
        }.groupBy({ it.first }, { it.second })
    }

    private fun Position.isInBounds() =
        x in map.indices && y in map[0].indices

    fun calculateAntinodes(all: Boolean = false): Long =
        buildSet {
            antenas.values.forEach { positions ->
                positions.forEach { antena ->
                    val others = positions - antena
                    when(all) {
                        true -> antena.placeAllAntinodesFor(others, this)
                        false -> antena.placeAntinodesFor(others, this)
                    }
                }
            }
        }.size.toLong()

    private fun Position.placeAntinodesFor(
        others: List<Position>,
        antinodesLocations: MutableSet<Position>
    ) {
        others.forEach { other ->
            val antinodePosition = this * 2 - other
            if (antinodePosition.isInBounds())
                antinodesLocations.add(antinodePosition)

        }
    }

    private fun Position.placeAllAntinodesFor(
        others: List<Position>,
        antinodesLocations: MutableSet<Position>
    ) {
        others.forEach { other ->
            generateSequence(0) { it + 1 }
                .map { scale -> this + (this - other) * scale }
                .takeWhile { it.isInBounds() }
                .forEach { antinodesLocations.add(it) }
        }
    }
}

fun main() {
    fun solvePartOne(roof: Roof): Long {
        return roof.calculateAntinodes()
    }

    fun solvePartTwo(roof: Roof): Long {
        return roof.calculateAntinodes(all = true)
    }

    val lines: List<String> = readInput("Day08")
    val roof = Roof(lines.map { line -> line.toMutableList() }.toMutableList())

    "---------- Part 1 ----------".println()
    solvePartOne(roof).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(roof).println()
}