import utils.parseNumbers
import utils.println
import utils.readInput

fun main() {
    fun String.splitToLong(): Pair<Long, Long>{
        val mid = length / 2
        return substring(0, mid).toLong() to substring(mid).toLong()
    }
    fun Long.change(): Pair<Long, Long?> = when {
        this == 0L -> 1L to null
        toString().length % 2 == 0 -> toString().splitToLong()
        else -> (this * 2024L) to null
    }

    fun MutableMap<Long, Long>.blink() {
        val changes = entries.map { (stone, count) ->
            val (first, second) = stone.change()
            listOfNotNull(first to count, second?.to(count))
        }.flatten()

        clear()
        changes.forEach { (stone, count) ->
            this[stone] = (this[stone] ?: 0) + count
        }
    }

    fun solvePartOne(input: MutableMap<Long, Long>): Long {
        repeat(25) {
            input.blink()
        }
        return input.map { it.value }.sum()
    }

    fun solvePartTwo(input: MutableMap<Long, Long>): Long {
        repeat(75) {
            input.blink()
        }
        return input.map { it.value }.sum()
    }

    val lines = readInput("Day11").first().parseNumbers(" ").map { it.toLong() }.toMutableList()
    val numbers = lines.groupBy { it }
        .mapValues { it.value.size.toLong() }
        .toMutableMap()
    val numbersCopy = lines.groupBy { it }
        .mapValues { it.value.size.toLong() }
        .toMutableMap()
    "---------- Part 1 ----------".println()
    solvePartOne(numbers).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(numbersCopy).println()
}