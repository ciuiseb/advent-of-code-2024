import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    val lines: List<String> = readInput("Day01")
    val result: Int = solveParteTwo(lines)
    result.println()
}

fun solvePartOne(input: List<String>): Int {
    val (leftList, rightList) = getHeapsFromInput(input)
    var result = 0

    if (leftList.size != rightList.size) {
        return 0
    }

    repeat(leftList.size) {
        val (left, right) = leftList.poll() to rightList.poll()
        result += abs(left - right)
    }
    return result
}

fun solveParteTwo(input: List<String>): Int {
    val (leftList, rightList) = getListsFromInput(input)

    var result = 0
    if (leftList.size != rightList.size) {
        return 0
    }

    repeat(leftList.size) {
        val left = leftList.removeFirst()
        val count = rightList.count{it == left}

        result += left * count
    }
    return result
}

fun getHeapsFromInput(input: List<String>)
        : Pair<PriorityQueue<Int>, PriorityQueue<Int>> {
    val firstList = PriorityQueue<Int>()
    val secondList = PriorityQueue<Int>()

    input.asSequence()
        .map { it.split("   ") }
        .forEach { (first, second) ->
            firstList.add(first.toInt())
            secondList.add(second.toInt())
        }

    return Pair(firstList, secondList)
}

fun getListsFromInput(input: List<String>)
        : Pair<MutableList<Int>, MutableList<Int>> {
    val firstList = mutableListOf<Int>()
    val secondList = mutableListOf<Int>()

    input.asSequence()
        .map { it.split("   ") }
        .forEach { (first, second) ->
            firstList.add(first.toInt())
            secondList.add(second.toInt())
        }
    return Pair(firstList, secondList)
}