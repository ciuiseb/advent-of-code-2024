import kotlin.math.abs

fun main() {
    fun isSafe(numbers: List<Int>): Boolean {
        val sign = if (numbers[0] < numbers[1]) 1 else -1

        return numbers.windowed(2)
            .all { (first, second) ->
                val difference = second - first
                difference * sign > 0 && abs(difference) in 1..3
            }

    }

    fun isSafeWithProblemDampener(numbers: List<Int>): Boolean {
        return isSafe(numbers)
                || numbers.indices.any { index ->
            isSafe(numbers.take(index) + numbers.drop(index + 1))
        }
    }

    fun solvePartOne(input: List<String>): Int {
        return input.asSequence()
            .map {
                it.split(" ")
                    .map(String::toInt)
            }
            .filter { isSafe(it) }
            .count()
    }

    fun solvePartTwo(input: List<String>): Int {
        return input.asSequence()
            .map {
                it.split(" ")
                    .map(String::toInt)
            }
            .filter { isSafeWithProblemDampener(it) }
            .count()
    }

    val lines: List<String> = readInput("Day02")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}