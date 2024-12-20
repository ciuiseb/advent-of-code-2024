import utils.*

data class State(val pos: Position, val dir: Direction)

enum class Direction(val dx: Int, val dy: Int) {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    fun turnRight() = entries[(ordinal + 1) % entries.size]
    fun turnLeft() = entries[(ordinal + entries.size - 1) % entries.size]
}

class ReindeerMaze(input: List<String>) : Grid<Char>(input.map { it.toMutableList() }.toMutableList()) {
    val start = getPositionOf('S')
    val end = getPositionOf('E')

    private fun getPositionOf(target: Char): Position {
        for (x in map.indices) {
            for (y in map[0].indices) {
                if (get(Position(x, y)) == target) return Position(x, y)
            }
        }
        error("")
    }

    private fun isValid(p: Position): Boolean =
        isInBounds(p) && get(p) != '#'

    fun getNeighbors(current: State): List<Pair<State, Int>> = buildList {
        val nextPos = Position(
            current.pos.x + current.dir.dx,
            current.pos.y + current.dir.dy
        )
        if (isValid(nextPos)) {
            add(State(nextPos, current.dir) to 1)
        }

        add(State(current.pos, current.dir.turnLeft()) to 1000)
        add(State(current.pos, current.dir.turnRight()) to 1000)
    }
}

fun dijkstra(
    start: State,
    isEnd: (State) -> Boolean,
    getNeighbors: (State) -> List<Pair<State, Int>>,
    getStateKey: (State) -> Any = { it }
): Int {
    val visited = mutableSetOf<Any>()
    val queue = java.util.PriorityQueue<Pair<State, Int>>(compareBy { it.second })
    queue.offer(start to -1000)

    while (queue.isNotEmpty()) {
        val (current, cost) = queue.poll()
        if (isEnd(current)) return cost

        val stateKey = getStateKey(current)
        if (stateKey in visited) continue
        visited.add(stateKey)

        for ((next, nextCost) in getNeighbors(current)) {
            queue.offer(next to cost + nextCost)
        }
    }

    return Int.MAX_VALUE
}

fun dijkstraTracked(
    start: State,
    isEnd: (State) -> Boolean,
    getNeighbors: (State) -> List<Pair<State, Int>>,
    getStateKey: (State) -> Any = { it }
): Int {
    val paths = mutableMapOf<State, MutableSet<List<State>>>()
    val costs = mutableMapOf<Any, Int>()
    val queue = java.util.PriorityQueue<Triple<State, Int, List<State>>>(compareBy { it.second })

    queue.offer(Triple(start, 0, listOf(start)))
    paths[start] = mutableSetOf(listOf(start))
    costs[getStateKey(start)] = 0

    var minEndCost = Int.MAX_VALUE
    val optimalPaths = mutableSetOf<List<State>>()

    while (queue.isNotEmpty()) {
        val (current, cost, path) = queue.poll()
        val stateKey = getStateKey(current)

        if (cost > minEndCost) continue
        if (stateKey in costs && costs[stateKey]!! < cost) continue
        if (isEnd(current)) {
            when {
                cost < minEndCost -> {
                    minEndCost = cost
                    optimalPaths.clear()
                    optimalPaths.add(path)
                }
                else -> {
                    optimalPaths.add(path)
                }
            }
            continue
        }

        for ((next, nextCost) in getNeighbors(current)) {
            val newCost = cost + nextCost
            val nextKey = getStateKey(next)

            if (nextKey in costs && costs[nextKey]!! < newCost) continue

            costs[nextKey] = costs[nextKey]?.let { minOf(it, newCost) } ?: newCost

            queue.offer(Triple(next, newCost, path + next))
        }
    }

    return optimalPaths.flatten().map{it.pos}.toSet().count()
}

fun main() {
    fun solvePartOne(input: List<String>): Int {
        val maze = ReindeerMaze(input)

        return dijkstra(
            start = State(maze.start, Direction.EAST),
            isEnd = { it.pos == maze.end },
            getStateKey = { it.pos to it.dir },
            getNeighbors = maze::getNeighbors
        )
    }

    fun solvePartTwo(input: List<String>): Int {
        val maze = ReindeerMaze(input)

        return dijkstraTracked(
            start = State(maze.start, Direction.EAST),
            isEnd = { it.pos == maze.end },
            getStateKey = { it.pos to it.dir },
            getNeighbors = maze::getNeighbors
        )
    }

    val lines: List<String> = readInput("Day16")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}