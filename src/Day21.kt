import utils.*
import kotlin.math.abs

data class Keypad(val grid: Array<Array<Char>>) {
    val positions = grid.flatMapIndexed { y, row ->
        row.mapIndexed { x, char -> char to Position(x, y) }
    }.toMap()
    val gapPosition = positions[' '] ?: error("")
}

class KeypadPathFinder {
    private val directionKeypad = Keypad(arrayOf(
        arrayOf(' ', '^', 'A'),
        arrayOf('<', 'v', '>')
    ))

    private val numericKeypad = Keypad(arrayOf(
        arrayOf('7', '8', '9'),
        arrayOf('4', '5', '6'),
        arrayOf('1', '2', '3'),
        arrayOf(' ', '0', 'A')
    ))

    private val cache = mutableMapOf<Triple<String, Int, Boolean>, Long>()

    private fun nonemptyRange(start: Int, end: Int) =
        if (start < end) start..<end else start downTo end + 1

    private fun getMoveOptions(from: Position, to: Position, gap: Position) = buildList {
        val (dx, dy) = (to.x - from.x) to (to.y - from.y)
        val horizontalArrow = if (dx > 0) ">" else "<"
        val verticalArrow = if (dy > 0) "v" else "^"

        when {
            from == to -> add("A")
            dx == 0 -> add(verticalArrow.repeat(abs(dy)) + "A")
            dy == 0 -> add(horizontalArrow.repeat(abs(dx)) + "A")
            else -> {
                if (!((gap.x == to.x && gap.y in nonemptyRange(from.y, to.y)) ||
                            (gap.y == from.y && gap.x in nonemptyRange(to.x, from.x)))) {
                    add(horizontalArrow.repeat(abs(dx)) + verticalArrow.repeat(abs(dy)) + "A")
                }
                if (!((gap.x == from.x && gap.y in nonemptyRange(to.y, from.y)) ||
                            (gap.y == to.y && gap.x in nonemptyRange(from.x, to.x)))) {
                    add(verticalArrow.repeat(abs(dy)) + horizontalArrow.repeat(abs(dx)) + "A")
                }
            }
        }
    }

    private fun getAllSequences(code: String, keypad: Keypad, start: Char = 'A'): List<List<String>> {
        if (code.isEmpty()) return listOf(emptyList())

        val from = keypad.positions[start] ?: error("")
        val to = keypad.positions[code[0]] ?: error("")

        return getMoveOptions(from, to, keypad.gapPosition).flatMap { option ->
            getAllSequences(code.substring(1), keypad, code[0]).map { listOf(option) + it }
        }
    }

    private fun findShortestSequence(code: String, iterations: Int, isNumpad: Boolean = true): Long {
        cache[Triple(code, iterations, isNumpad)]?.let { return it }

        val sequences = getAllSequences(code, if (isNumpad) numericKeypad else directionKeypad)
        val result = if (iterations == 0) {
            sequences.minOf { it.sumOf(String::length).toLong() }
        } else {
            sequences.minOf { it.sumOf { dircode -> findShortestSequence(dircode, iterations - 1, false) } }
        }

        return result.also { cache[Triple(code, iterations, isNumpad)] = it }
    }

    fun solveForNumber(number: String, proxies: Int) = findShortestSequence(number, proxies)
}

fun main() {
    fun solvePartOne(input: List<String>) = KeypadPathFinder().let { keypad ->
        input.sumOf { number ->
            keypad.solveForNumber(number, 2) * number.substringBefore('A').toLong()
        }
    }

    fun solvePartTwo(input: List<String>) = KeypadPathFinder().let { keypad ->
        input.sumOf { number ->
            keypad.solveForNumber(number, 25) * number.substringBefore('A').toLong()
        }
    }

    val lines: List<String> = readInput("Day21")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}