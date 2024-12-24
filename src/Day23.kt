import utils.println
import utils.readInput

fun main() {
    data class Edge(val from: String, val to: String)

    fun String.toEdge(): Edge =
        split("-").let { (from, to) -> Edge(from, to) }

    fun List<String>.toGraph(): Map<String, Set<String>> =
        filter { it.isNotEmpty() }
            .map { it.toEdge() }
            .fold(mutableMapOf<String, MutableSet<String>>()) { graph, (from, to) ->
                graph.apply {
                    this.getOrPut(from) { mutableSetOf() } += to
                    this.getOrPut(to) { mutableSetOf() } += from
                }
            }

    fun Map<String, Set<String>>.findTriangles(): Set<Set<String>> {
        val nodes = keys.sorted()
        return buildSet {
            for (node1 in nodes) {
                getValue(node1)
                    .filter { node2 -> node2 > node1 }
                    .forEach { node2 ->
                        getValue(node1).intersect(getValue(node2))
                            .filter { node3 -> node3 > node2 }
                            .mapTo(this) { node3 ->
                                setOf(node1, node2, node3)
                            }
                    }
            }
        }
    }

    fun Map<String, Set<String>>.isClique(nodes: Set<String>): Boolean =
        nodes.all { node ->
            val neighbors = getValue(node)
            nodes.all { other -> other == node || other in neighbors }
        }

    fun Map<String, Set<String>>.findLargestClique(): Set<String> {
        val nodes = keys.toList()
        var currentMax = setOf<String>()

        fun tryGrowClique(current: Set<String>, candidates: List<String>) {
            if (current.size > currentMax.size && isClique(current)) {
                currentMax = current
            }

            candidates.forEachIndexed { index, node ->
                if (current.all { getValue(it).contains(node) }) {
                    tryGrowClique(current + node, candidates.drop(index + 1))
                }
            }
        }

        tryGrowClique(emptySet(), nodes)
        return currentMax
    }

    fun solvePartOne(input: List<String>): Int =
        input.toGraph()
            .findTriangles()
            .count { triple -> triple.any { it.startsWith('t') } }

    fun solvePartTwo(input: List<String>): String =
        input.toGraph()
            .findLargestClique()
            .sorted()
            .joinToString(",")

    val lines: List<String> = readInput("Day23")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}