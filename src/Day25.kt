import utils.println
import utils.readInput

fun main() {
    fun parsePatterns(input: List<String>): List<List<String>> {
        val patterns = mutableListOf<List<String>>()
        val currentPattern = mutableListOf<String>()

        for (line in input) {
            if (line.isEmpty()) {
                if (currentPattern.isNotEmpty()) {
                    patterns.add(currentPattern.toList())
                    currentPattern.clear()
                }
            } else {
                currentPattern.add(line)
            }
        }
        if (currentPattern.isNotEmpty()) {
            patterns.add(currentPattern.toList())
        }
        return patterns
    }

    fun getColumnHeights(pattern: List<String>): List<Int> {
        val width = pattern[0].length
        val heights = MutableList(width) { 0 }

        for (col in 0..<width) {
            var maxHeight = 0
            var currentCount = 0
            for (row in pattern.size - 1 downTo 0) {
                if (pattern[row][col] == '#') {
                    currentCount++
                    maxHeight = maxOf(maxHeight, currentCount)
                } else {
                    currentCount = 0
                }
            }
            heights[col] = maxHeight
        }
        return heights
    }

    fun parseToLocksAndKeys(input: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
        val patterns = parsePatterns(input)
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()

        patterns.forEach { pattern ->
            val heights = getColumnHeights(pattern)
            if (pattern.first().contains('#')) {
                locks.add(heights)
            } else {
                keys.add(heights)
            }
        }

        return locks to keys
    }

    fun createMatchMap(locks: List<List<Int>>, maxHeight: Int = 7): Map<Pair<Int, Int>, List<List<Int>>> {
        val matchMap = mutableMapOf<Pair<Int, Int>, List<List<Int>>>()

        for (col in 0 until 5) {
            for (keyHeight in 1..maxHeight) {
                val matchingLocks: MutableList<List<Int>> = mutableListOf()

                locks.forEachIndexed { lockIndex, lock ->
                    val lockHeight = lock[col]
                    if (keyHeight + lockHeight <= maxHeight) {
                        matchingLocks.add(lock)
                    }
                }

                if (matchingLocks.isNotEmpty()) {
                    matchMap[Pair(col, keyHeight)] = matchingLocks
                }
            }
        }

        return matchMap
    }
    fun solvePartOne(input: List<String>):Int {
        val (locks, keys) = parseToLocksAndKeys(input)
        val matches = createMatchMap(locks)

        return keys.sumOf { key ->
            val validLocks = mutableSetOf<List<Int>>()

            validLocks.addAll(matches[0 to key.first()] ?: emptyList())

            for (col in 1..<key.size) {
                validLocks.retainAll(matches[col to key[col]] ?: emptyList())
            }

            validLocks.size
        }
    }

    val lines: List<String> = readInput("Day25")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
}