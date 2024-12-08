import utils.middle
import utils.parseNumbers
import utils.println
import utils.readInput

fun main() {

    fun getRules(rulesList: List<String>): Map<Int, List<Int>> = rulesList
        .map { it.split("|") }
        .groupBy(
            keySelector = { it[0].toInt() },
            valueTransform = { it[1].toInt() }
        )

    fun getRulesAndUpdates(input: List<String>): Pair<Map<Int, List<Int>>, List<List<Int>>> {
        val rulesStringList = input.takeWhile { it.isNotEmpty() } // takes untill the blank line is found
        val rules = getRules(rulesStringList)
        val updates = input.dropWhile { it.isNotEmpty() }
            .drop(1) // takes evrything after finding the blank line, then drops it
            .map { it.parseNumbers(",") }

        return Pair(rules, updates)
    }

    fun Int.cantComeBefore(nextPage: Int, rules: Map<Int, List<Int>>): Boolean =
        rules[nextPage]?.contains(this) == true

    fun List<Int>.hasCorrectPageOrder(rules: Map<Int, List<Int>>): Boolean {
        forEachIndexed { index, currentPage ->
            val nextPages = this.drop(index)

            if (nextPages.any { nextPage -> currentPage.cantComeBefore(nextPage, rules) }) {
                return false
            }
        }
        return true
    }


    fun List<Int>.withCorrectedPageOrder(rules: Map<Int, List<Int>>): List<Int> {
        var result = mutableListOf<Int>()
        var remaining = toMutableList()

        while (remaining.isNotEmpty()) {
            val nextPage = remaining.find { page ->
                remaining.none { otherPage ->
                    page.cantComeBefore(otherPage, rules)
                }
            } ?: error("")

            result.add(nextPage)
            remaining.remove(nextPage)
        }
        return result
    }

    fun solvePartOne(rules: Map<Int, List<Int>>, updates: List<List<Int>>): Long = updates
        .sumOf { update ->
            update.takeIf { it.hasCorrectPageOrder(rules) }
                ?.middle()?.toLong()
                ?: 0L
        }

    fun solvePartTwo(rules: Map<Int, List<Int>>, updates: List<List<Int>>): Long = updates
        .sumOf { update ->
            update.takeUnless() { it.hasCorrectPageOrder(rules) }
                ?.withCorrectedPageOrder(rules)
                ?.middle()?.toLong()
                ?: 0L
        }

    val lines: List<String> = readInput("Day05")
    val (rules, updates) = getRulesAndUpdates(lines)

    "---------- Part 1 ----------".println()
    solvePartOne(rules, updates).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(rules, updates).println()
}