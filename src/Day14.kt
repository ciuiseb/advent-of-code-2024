import utils.Position
import utils.println
import utils.readInput
import utils.substringBetween

data class BathroomSecurity(var pos: Position, val velocity: Position) {

    fun teleport(boundaries: Boundaries) {
        var x = pos.x % boundaries.row
        if (x < 0) x += boundaries.row

        var y = pos.y % boundaries.column
        if (y < 0) y += boundaries.column
        pos = Position(x, y)
    }

    fun walk(nrOfSteps: Int = 1) {
        pos += velocity * nrOfSteps
    }
}

fun BathroomSecurity.walkIn(boundaries: Boundaries, steps: Int) {
    walk(nrOfSteps = steps)
    teleport(boundaries)
}

data class Boundaries(val row: Int, val column: Int)

fun main() {

    fun getBathroomSecurity(lines: List<String>): List<BathroomSecurity> = buildList {
        lines.forEach { line ->
            val parts = line.split(" ")
            if (parts.size == 2) {
                val positionString = parts[0]
                val velocityString = parts[1]

                val position = Position(
                    positionString.substringBetween("p=", ",").trim().toInt(),
                    positionString.substringAfter(",").trim().toInt()
                )
                val velocity = Position(
                    velocityString.substringBetween("v=", ",").trim().toInt(),
                    velocityString.substringAfter(",").trim().toInt(),
                )
                add(BathroomSecurity(position, velocity))
            }
        }
    }

    fun groupInCadrans(boundaries: Boundaries, bathroomSecurity: MutableList<BathroomSecurity>) =
        bathroomSecurity.groupBy { security ->
            val midRow = (boundaries.row - 1) / 2
            val midCol = (boundaries.column - 1) / 2

            with(security.pos) {
                when {
                    x == midRow || y == midCol -> null
                    x < midRow && y > midCol -> 1
                    x < midRow && y < midCol -> 2
                    x > midRow && y < midCol -> 3
                    x > midRow && y > midCol -> 4
                    else -> null
                }
            }
        }

    fun getSafetyFactor(boundaries: Boundaries, bathroomSecurity: MutableList<BathroomSecurity>): Long {
        var result = 1L
        groupInCadrans(boundaries, bathroomSecurity)
            .filterKeys { it != null }
            .mapKeys { it.value.distinct() }
            .forEach { result *= it.value.size }
        return result
    }

    fun solvePartOne(boundaries: Boundaries, bathroomSecurity: MutableList<BathroomSecurity>): Long {
        bathroomSecurity.forEach { robot ->
            robot.walkIn(boundaries, 100)
        }
        return getSafetyFactor(boundaries, bathroomSecurity)
    }

    fun solvePartTwo(boundaries: Boundaries, bathroomSecurity: MutableList<BathroomSecurity>): Int {
        var lowestSafetyFactor = Long.MAX_VALUE
        var targetPosition = 0
        (0..(boundaries.row * boundaries.column)).forEach { index ->
            val safetyFactor = getSafetyFactor(boundaries, bathroomSecurity)
            if (safetyFactor in 0..lowestSafetyFactor) {
                lowestSafetyFactor = safetyFactor
                targetPosition = index
            }

            bathroomSecurity.forEach {
                it.walk()
                it.teleport(boundaries)
            }
        }
        return targetPosition
    }

    val lines: List<String> = readInput("Day14")
    val (rows, cols) = 101 to 103

    "---------- Part 1 ----------".println()
    solvePartOne(Boundaries(rows, cols), getBathroomSecurity(lines).toMutableList()).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(Boundaries(rows, cols), getBathroomSecurity(lines).toMutableList()).println()
}