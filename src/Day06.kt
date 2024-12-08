import utils.*

enum class GuardDirection(val symbol: Char) {
    FORWARD('^'),
    BACKWARD('v'),
    RIGHT('>'),
    LEFT('<');

    companion object {
        fun fromSymbol(symbol: Char): GuardDirection =
            entries.find { it.symbol == symbol } ?: error("$symbol is not a direction")
    }

    fun getOffset(): Position = when (this) {
        FORWARD -> Position(-1, 0)
        BACKWARD -> Position(1, 0)
        RIGHT -> Position(0, 1)
        LEFT -> Position(0, -1)
    }

    fun next(): GuardDirection = when (this) {
        FORWARD -> RIGHT
        RIGHT -> BACKWARD
        BACKWARD -> LEFT
        LEFT -> FORWARD
    }
}

data class Guard(
    var position: Position,
    var direction: GuardDirection
) {
    fun nextPosition(): Position = (direction.getOffset().let { position + it })

    fun walk() {
        position = nextPosition()
    }

    fun canWalk(map: List<List<Char>>): Boolean = map.get(nextPosition()) != '#'

    fun changeDirection() {
        direction = direction.next()
    }
}

data class Lab(
    val map: MutableList<MutableList<Char>>
) {
    fun visitGuardPosition(guard: Guard) {
        map.set(guard.position, 'X')
    }

    fun findGuard(): Position = map.indices
        .firstNotNullOf { row ->
            map[row].indices.find { column ->
                val char = map[row][column]
                char != '.' && char != '#' && char != 'X'
            }?.let { col -> Position(row, col) }
        }

    fun getGuardInitialPosition(): Guard = Guard(
        findGuard(),
        GuardDirection.fromSymbol(map.get(findGuard()))
    )

    fun canPlaceObstructionAt(guard: Guard, map: List<List<Char>>): Boolean = runCatching {
        repeat(10000) {
            if (guard.canWalk(map)) {
                guard.walk()
            } else {
                guard.changeDirection()
            }
        }
        true
    }.getOrDefault(false)

}

fun main() {
    fun solvePartOne(lab: Lab): Long {
        val guard = lab.getGuardInitialPosition()
        runCatching {
            while (true) {
                lab.visitGuardPosition(guard)
                if (guard.canWalk(lab.map)) {
                    guard.walk()
                } else {
                    guard.changeDirection()
                }
            }
        }
        return lab.map.sumOf { row -> row.count { it == 'X' } }.toLong()
    }

    fun solvePartTwo(lab: Lab): Long {
        val guard = lab.getGuardInitialPosition()
        val potentialObstructions = mutableListOf<Position>()

        lab.map.forEachIndexed { x, row ->
            row.forEachIndexed { y, cell ->
                if (cell == '.') {
                    potentialObstructions.add(Position(x, y))
                }
            }
        }

        return potentialObstructions.count { obstruction ->
            val tempMap = lab.map.map { it.toMutableList() }.toMutableList()
            tempMap[obstruction.x][obstruction.y] = '#'

            lab.canPlaceObstructionAt(guard.copy(), tempMap)
        }.toLong()
    }

    val lines = readInput("Day06")
    val map = lines.map { line -> line.toMutableList() }.toMutableList()

    "---------- Part 1 ----------".println()
    solvePartOne(Lab(map.map { it.toMutableList() }.toMutableList())).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(Lab(map.map { it.toMutableList() }.toMutableList())).println()
}