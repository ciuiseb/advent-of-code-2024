import utils.println
import utils.readInput

fun main() {
    fun parseInput(input: List<String>): Pair<List<String>, List<String>> {
        val patterns = input[0].split(", ").map { it.trim() }
        val designs = input.dropWhile { it.isNotEmpty() }.filter { it.isNotEmpty() }
        return patterns to designs
    }
    fun canMakeDesign(design: String, patterns: List<String>, memo: MutableMap<String, Boolean> = mutableMapOf()): Boolean {
        if (design.isEmpty()) return true
        if (design in memo) return memo[design]!!

        for (pattern in patterns) {
            if (design.startsWith(pattern)) {
                if (canMakeDesign(design.substring(pattern.length), patterns, memo)) {
                    memo[design] = true
                    return true
                }
            }
        }
        memo[design] = false
        return false
    }
    fun countWaysToMakeDesign(design: String, patterns: List<String>, memo: MutableMap<String, Long> = mutableMapOf()): Long {
        if (design.isEmpty()) return 1L
        if (design in memo) return memo[design]!!

        var totalWays = 0L
        for (pattern in patterns) {
            if (design.startsWith(pattern)) {
                totalWays += countWaysToMakeDesign(design.substring(pattern.length), patterns, memo)
            }
        }
        memo[design] = totalWays
        return totalWays
    }
    fun solvePartOne(input: List<String>): Int {
        val (patterns, designs) = parseInput(input)
        return designs.count { design ->
            canMakeDesign(design, patterns)
        }
    }

    fun solvePartTwo(input: List<String>): Long {
        val (patterns, designs) = parseInput(input)
        return designs.sumOf { design ->
            countWaysToMakeDesign(design, patterns)
        }
    }

    val lines: List<String> = readInput("Day19")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}