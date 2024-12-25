import utils.println
import utils.readInput

data class ConnectionData(
    val firstGate: String,
    val operator: String,
    val secondGate: String,
    val destinationGate: String
)

fun List<String>.getInitialValues() = takeWhile { it.isNotEmpty() }
    .associate {
        val (name, value) = it.split(": ")
        name to value
    }.toMutableMap()

fun String.parseConnection(): ConnectionData {
    val operator = when {
        contains(" XOR ") -> "XOR"
        contains(" AND ") -> "AND"
        contains(" OR ") -> "OR"
        else -> error(this)
    }
    val parts = this.split(" $operator ", "->").map { it.trim() }
    return ConnectionData(parts[0], operator, parts[1], parts[2])
}

fun List<String>.getGatesConnections() = dropWhile { it != "" }.drop(1).map { it.parseConnection() }

fun String.solveDependencies(
    dependencies: MutableMap<String, MutableList<String>>,
    gates: MutableMap<String, String>,
    connections: List<ConnectionData>
) {
    dependencies[this]?.forEach { destinationGate ->
        val connection = connections.find { it.destinationGate == destinationGate } ?: return@forEach

        val containsFirst = gates[connection.firstGate]?.isNotEmpty() ?: false
        val containsSecond = gates[connection.secondGate]?.isNotEmpty() ?: false

        if (containsFirst && containsSecond) {
            val result = when (connection.operator) {
                "XOR" -> (gates[connection.firstGate]?.toIntOrNull() ?: 0) xor
                        (gates[connection.secondGate]?.toIntOrNull() ?: 0)

                "AND" -> (gates[connection.firstGate]?.toIntOrNull() ?: 0) and
                        (gates[connection.secondGate]?.toIntOrNull() ?: 0)

                "OR" -> (gates[connection.firstGate]?.toIntOrNull() ?: 0) or
                        (gates[connection.secondGate]?.toIntOrNull() ?: 0)

                else -> error("")
            }.toString()

            gates[destinationGate] = result
            destinationGate.solveDependencies(dependencies, gates, connections)
        }
    }
}

fun List<ConnectionData>.wait(
    gates: MutableMap<String, String>,
    dependencies: MutableMap<String, MutableList<String>>
) {
    forEach { (first, operator, second, destination) ->
        val containsFirst = gates[first]?.isNotEmpty() ?: false
        val containsSecond = gates[second]?.isNotEmpty() ?: false

        if (containsFirst && containsSecond) {
            val result = when (operator) {
                "XOR" -> (gates[first]?.toIntOrNull() ?: 0) xor (gates[second]?.toIntOrNull() ?: 0)
                "AND" -> (gates[first]?.toIntOrNull() ?: 0) and (gates[second]?.toIntOrNull() ?: 0)
                "OR" -> (gates[first]?.toIntOrNull() ?: 0) or (gates[second]?.toIntOrNull() ?: 0)
                else -> error("")
            }.toString()
            gates[destination] = result
            destination.solveDependencies(dependencies, gates, this)
        }

        dependencies.getOrPut(first) { mutableListOf() }.add(destination)

        dependencies.getOrPut(second) { mutableListOf() }.add(destination)

    }
}
fun main() {
    fun solvePartOne(input: List<String>): Long {
        val gates = input.getInitialValues()

        input.getGatesConnections().wait(gates, mutableMapOf())
        return gates.keys
            .filter { it.startsWith('z') }
            .sorted()
            .reversed()
            .map { gates[it] }
            .joinToString("")
            .toLong(2)
    }

    fun solvePartTwo(input: List<String>): String {
        val wires = mutableMapOf<String, Int>()
        val operations = mutableListOf<List<String>>()

        fun process(op: String, op1: Int, op2: Int): Int = when(op) {
            "AND" -> op1 and op2
            "OR" -> op1 or op2
            "XOR" -> op1 xor op2
            else -> error("Unknown operator: $op")
        }

        var highestZ = "z00"

        input.forEach { line ->
            if (":" in line) {
                val (wire, value) = line.split(": ")
                wires[wire] = value.toInt()
            } else if ("->" in line) {
                val parts = line.split(" ")
                val (op1, op, op2, _, res) = parts
                operations.add(listOf(op1, op, op2, res))
                if (res[0] == 'z' && res.substring(1).toInt() > highestZ.substring(1).toInt()) {
                    highestZ = res
                }
            }
        }

        val wrong = mutableSetOf<String>()

        operations.forEach { (op1, op, op2, res) ->
            if (res[0] == 'z' && op != "XOR" && res != highestZ) {
                wrong.add(res)
            }
            if (op == "XOR" &&
                res[0] !in listOf('x', 'y', 'z') &&
                op1[0] !in listOf('x', 'y', 'z') &&
                op2[0] !in listOf('x', 'y', 'z')) {
                wrong.add(res)
            }
            if (op == "AND" && "x00" !in listOf(op1, op2)) {
                operations.forEach inner@{ (subop1, subop, subop2, _) ->
                    if ((res == subop1 || res == subop2) && subop != "OR") {
                        wrong.add(res)
                    }
                }
            }
            if (op == "XOR") {
                operations.forEach inner@{ (subop1, subop, subop2, _) ->
                    if ((res == subop1 || res == subop2) && subop == "OR") {
                        wrong.add(res)
                    }
                }
            }
        }

        val opsList = operations.toMutableList()
        while (opsList.isNotEmpty()) {
            val (op1, op, op2, res) = opsList.removeAt(0)
            if (op1 in wires && op2 in wires) {
                wires[res] = process(op, wires[op1]!!, wires[op2]!!)
            } else {
                opsList.add(listOf(op1, op, op2, res))
            }
        }

        return wrong.sorted().joinToString(",")
    }


    val lines: List<String> = readInput("Day24")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}