import utils.Position
import utils.println
import utils.readInput
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

class ClawMachine(
    private val buttonA: Position,
    private val buttonB: Position,
    private val prize: Position
) {
    private fun calculateButtonPressesForPrize(): Pair<Long, Long> {
        val m = (prize.x * buttonB.y - prize.y * buttonB.x) / (buttonA.x * buttonB.y - buttonA.y * buttonB.x).toDouble()
        val n = (prize.x - buttonA.x * m) / buttonB.x.toDouble()
        if (m != m.toLong().toDouble() || n != n.toLong().toDouble()) return 0L to 0L
        return m.toLong() to n.toLong()
    }

    fun calculateMinimumCost(): Long {
        val steps = calculateButtonPressesForPrize()
        if (steps == 0L to 0L) return 0L
        return (3 * steps.first + steps.second)
    }

    private fun calculateButtonPressesWithHellaOffset(): Pair<BigInteger, BigInteger> {
        val newPrize = Pair(
            BigDecimal(prize.x).add(BigDecimal("10000000000000")),
            BigDecimal(prize.y).add(BigDecimal("10000000000000"))
        )
        val newButtonA = Pair(
            BigDecimal(buttonA.x),
            BigDecimal(buttonA.y)
        )
        val newButtonB = Pair(
            BigDecimal(buttonB.x),
            BigDecimal(buttonB.y)
        )

        val denominator = newButtonA.first.multiply(newButtonB.second)
            .subtract(newButtonA.second.multiply(newButtonB.first))

        val numeratorM = newPrize.first.multiply(newButtonB.second)
            .subtract(newPrize.second.multiply(newButtonB.first))

        val m = numeratorM.divide(denominator, MathContext.DECIMAL128)
        val n = newPrize.first.subtract(newButtonA.first.multiply(m))
            .divide(newButtonB.first, MathContext.DECIMAL128)

        if (m.stripTrailingZeros().scale() <= 0 && n.stripTrailingZeros().scale() <= 0) {
            return Pair(m.toBigInteger(), n.toBigInteger())
        }
        return BigInteger.ZERO to BigInteger.ZERO
    }

    fun calculateMinimumCostWithOffset(): BigInteger {
        val steps = calculateButtonPressesWithHellaOffset()
        if (steps == BigInteger.ZERO to BigInteger.ZERO) return BigInteger.ZERO
        return BigInteger.valueOf(3).multiply(steps.first).add(steps.second)
    }
}

fun main() {
    fun String.substringBetween(after: String, before: String) = substringAfter(after).substringBefore(before)
    fun getClawMachines(groups: List<List<String>>): List<ClawMachine> = buildList {
        groups.map { group ->
            val stringA = group[0]
            val stringB = group[1]
            val stringPrize = group[2]

            val buttonA = Position(
                stringA.substringBetween(" X+", ",").toInt(),
                stringA.substringAfter(" Y+").toInt()
            )

            val buttonB = Position(
                stringB.substringBetween(" X+", ",").toInt(),
                stringB.substringAfter(" Y+").toInt()
            )
            val prize = Position(
                stringPrize.substringBetween(" X=", ",").toInt(),
                stringPrize.substringAfter("Y=").toInt()
            )
            add(ClawMachine(buttonA, buttonB, prize))
        }
    }

    fun solvePartOne(clawMachines: List<ClawMachine>): Long =
        clawMachines.sumOf { it.calculateMinimumCost() }

    fun solvePartTwo(clawMachines: List<ClawMachine>): BigInteger =
        clawMachines.sumOf { it.calculateMinimumCostWithOffset() }

    val lines: List<String> = readInput("Day13")
    val groups = lines.chunked(4)


    "---------- Part 1 ----------".println()
    solvePartOne(getClawMachines(groups)).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(getClawMachines(groups)).println()
}