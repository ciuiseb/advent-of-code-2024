import utils.println
import utils.readInput

val evolutions = mutableMapOf<Long, Long>()

data class SecretNumber(var value: Long) {
    fun generateNewSecretNumber(times: Int = 1) = repeat(times) {
        if (value !in evolutions.keys) evolutions[value] = evolve()
        value = evolutions[value]!!
    }

    private fun evolve(): Long {
        var copy = value
        val first = copy * 64
        copy = mix(copy, first)
        copy = prune(copy)

        val second = copy / 32
        copy = mix(copy, second)
        copy = prune(copy)

        val third = copy * 2048
        copy = mix(copy, third)
        copy = prune(copy)

        return copy
    }

    private fun mix(firstNumber: Long, secondNumber: Long) = firstNumber xor secondNumber
    private fun prune(number: Long) = number % 16777216

}

fun main() {
    fun getSequenceBeforePrice(changes: List<Int>, index: Int): List<Int>? {
        if (index < 4) return null
        return changes.subList(index - 4, index)
    }

    fun findPriceAfterSequence(
        changes: List<Int>,
        prices: List<Long>,
        sequence: List<Int>
    ): Int? {
        for (i in 0..changes.size - sequence.size) {
            if (changes.subList(i, i + sequence.size) == sequence) {
                return (prices[i + sequence.size] % 10).toInt()
            }
        }
        return null
    }

    fun solvePartOne(input: List<String>): Long {
        val numbers = input.map { SecretNumber(it.toLong()) }
        return numbers.sumOf {
            it.generateNewSecretNumber(2000)
            it.value

        }
    }

    fun solvePartTwo(input: List<String>): Int {


        val buyers = input.map { it.toLong() }
        val allBuyersData = buyers.map { buyer ->
            val secret = SecretNumber(buyer)
            val prices = mutableListOf(secret.value)
            repeat(2000) {
                secret.generateNewSecretNumber()
                prices.add(secret.value)
            }
            val priceDigits = prices.map { it % 10 }
            val changes = priceDigits.windowed(2).map { (a, b) -> (b - a).toInt() }
            Triple(changes, prices, priceDigits)
        }

        val sequencesBeforeMaxima = mutableMapOf<List<Int>, Int>()

        for ((changes, _, priceDigits) in allBuyersData) {
            for (i in 4..<priceDigits.size) {
                val currentPrice = priceDigits[i].toInt()
                val isLocalMax = (i - 3..i + 3).all { j ->
                    j == i || j >= priceDigits.size || currentPrice >= priceDigits[j].toInt()
                }

                if (isLocalMax) {
                    val sequence = getSequenceBeforePrice(changes, i - 1)
                    if (sequence != null) {
                        sequencesBeforeMaxima[sequence] =
                            sequencesBeforeMaxima.getOrDefault(sequence, 0) + 1
                    }
                }
            }
        }

        var maxBananas = 0
        val topSequences = sequencesBeforeMaxima.entries
            .sortedByDescending { it.value }
            .take(100)
            .map { it.key }

        for (sequence in topSequences) {
            var totalBananas = 0
            for ((changes, prices, _) in allBuyersData) {
                val price = findPriceAfterSequence(changes, prices, sequence)
                if (price != null) {
                    totalBananas += price
                }
            }
            maxBananas = maxOf(maxBananas, totalBananas)
        }

        return maxBananas
    }

    val lines: List<String> = readInput("Day22")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}