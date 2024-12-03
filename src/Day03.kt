fun main() {
    class MemoryState(var enabled: Boolean = true)

    fun String.scanMultiplicationPattern(
        startIndex: Int,
        enabled: Boolean = true
    ): Pair<Long, Int> { // value to be added, next index
        val defaultReturnValue = Pair(0L, startIndex + 1)

        val endIndex = this.indexOf(")", startIndex)
        if (endIndex == -1) return defaultReturnValue

        return substring(startIndex + 4, endIndex)
            .split(",")
            .takeIf { it.size == 2 && it.none { it.contains(" ") } }
            ?.let { numbers ->
                runCatching {
                    val (first: Long, second: Long) = numbers.map { it.toLong() }
                    val value = if (enabled) first * second else 0L
                    Pair(value, endIndex + 1)
                }.getOrDefault(defaultReturnValue)
            } ?: return defaultReturnValue
    }

    fun scanCorruptedMemory(input: String): Long {
        var result = 0L
        var currentIndex = 0

        while (currentIndex < input.length) {
            input.indexOf("mul(", currentIndex)
                .takeIf { it != -1 }
                ?.let { startIndex ->
                    val (value: Long, nextIndex: Int) = input.scanMultiplicationPattern(startIndex)
                    result += value
                    currentIndex = nextIndex
                } ?: break

        }
        return result
    }

    fun scanEnabledCorruptedMemory(input: String, state: MemoryState): Long {
        var result = 0L
        var currentIndex = 0
        val patterns = listOf("do()", "don't()", "mul(")

        while (currentIndex < input.length) {
            input.findAnyOf(patterns, currentIndex)
                ?.let { (startIndex, pattern) ->
                    when (pattern) {
                        "do()" -> state.enabled = true.also { currentIndex = startIndex + pattern.length }
                        "don't()" -> state.enabled = false.also { currentIndex = startIndex + pattern.length }
                        "mul(" -> {
                            val (value: Long, nextIndex: Int) = input.scanMultiplicationPattern(
                                    startIndex,
                                    state.enabled
                                )
                            result += value
                            currentIndex = nextIndex
                        }
                    }
                } ?: break
        }
        return result
    }

    fun solvePartOne(input: List<String>): Long {
        return input.sumOf { scanCorruptedMemory(it) }
    }

    fun solvePartTwo(input: List<String>): Long {
        val state = MemoryState()
        return input.sumOf { scanEnabledCorruptedMemory(it, state) }
    }

    val lines: List<String> = readInput("Day03")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}