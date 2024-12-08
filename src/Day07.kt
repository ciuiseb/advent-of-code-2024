import utils.println
import utils.readInput

fun main() {
    fun Long.concatenate(other: Long): Long = (this.toString() + other.toString()).toLong()

    fun List<Long>.canComputeTo(testValue: Long, withConcatenation: Boolean = false): Boolean{
        fun backtrack(index: Int, current: Long): Boolean {
            if (current > testValue) return false
            if (index == size) return current == testValue

            return backtrack(index + 1, current * this[index]) ||
                    backtrack(index + 1, current + this[index]) ||
                    if(!withConcatenation) false else backtrack(index + 1, current.concatenate(this[index]))
        }
        return backtrack(1, this[0].toLong())
    }

    fun solvePartOne(calibrationData: List<Pair<Long, List<Long>>>): Long = calibrationData
        .sumOf { (testValue, equation) ->
            if (equation.canComputeTo(testValue)) testValue else 0L
        }


    fun solvePartTwo(calibrationData: List<Pair<Long, List<Long>>>): Long = calibrationData
        .sumOf { (testValue, numbers) ->
            if (numbers.canComputeTo(testValue, withConcatenation = true)) testValue else 0L
        }

    val lines: List<String> = readInput("Day07")
    val calibrationData = lines.map { line ->
        val parts = line.replace(":", "").split(" ")
        parts[0].toLong() to parts.drop(1).map { it.toLong() }
    }
    "---------- Part 1 ----------".println()
    solvePartOne(calibrationData).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(calibrationData).println()
}