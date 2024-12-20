import utils.*
import java.lang.StrictMath.pow

data class Instruction(val opcode: Int, val operator: Int)
data class ChronospatialComputer(
    var registerA: Long,
    var registerB: Long,
    var registerC: Long,
    val instructions: List<Instruction>
) {
    private var instructionsIndex = 0
    private val outputs = mutableListOf<Int>()

    fun getOperationsResult(): List<Int> {
        while (instructionsIndex in instructions.indices) {
            instructions[instructionsIndex].execute()
        }
        return outputs
    }

    private fun Instruction.execute() {
        val (opcode, operator) = this
        when (opcode) {
            0 -> adv(operator)
            1 -> bxl(operator)
            2 -> bst(operator)
            3 -> jnz(operator)
            4 -> bxc(operator)
            5 -> out(operator)
            6 -> bdv(operator)
            7 -> cdv(operator)
        }
    }

    private fun translateComboOperand(comboOperand: Int): Long? = when (comboOperand) {
        in 0..3 -> comboOperand.toLong()
        4 -> registerA
        5 -> registerB
        6 -> registerC
        else -> null
    }

    private fun adv(operand: Int) {
        registerA = (registerA / pow(2.0, translateComboOperand(operand)!!.toDouble())).toInt().toLong()
        instructionsIndex++
    }

    private fun bxl(operand: Int) {
        registerB = registerB xor operand.toLong()
        instructionsIndex++
    }

    private fun bst(operand: Int) {
        registerB = (translateComboOperand(operand)!! % 8)
        instructionsIndex++
    }

    private fun jnz(operand: Int) {
        if (registerA == 0L) {
            instructionsIndex++
            return
        }
        instructionsIndex = operand
    }

    private fun bxc(operand: Int) {
        registerB = registerB xor registerC
        instructionsIndex++
    }

    private fun out(operand: Int) {
        outputs.add((translateComboOperand(operand)!! % 8).toInt())
        instructionsIndex++
    }

    private fun bdv(operand: Int) {
        registerB = (registerA / pow(2.0, translateComboOperand(operand)!!.toDouble())).toLong()
        instructionsIndex++
    }

    private fun cdv(operand: Int) {
        registerC = (registerA / pow(2.0, translateComboOperand(operand)!!.toDouble())).toLong()
        instructionsIndex++
    }
}

fun main() {
    fun parseInput(input: List<String>): List<Any> {
        val regA = input.first().substringAfter("Register A: ").toLong()
        val registerB = input[1].substringAfter("Register B: ").toLong()
        val registerC = input[2].substringAfter("Register C: ").toLong()
        val instructions = input[4].substringAfter("Program: ")
            .parseNumbers(",")
            .windowed(size = 2, step = 2)
            .map { Instruction(it[0], it[1]) }

        return listOf(regA, registerB, registerC, instructions)
    }

    fun solvePartOne(input: List<String>):List<Int> {
        val parsed = parseInput(input)
        val regA = parsed[0] as Long
        val regB = parsed[1] as Long
        val regC = parsed[2] as Long
        val instructions = parsed[3] as List<Instruction>
        val computer = ChronospatialComputer(regA, regB, regC, instructions)

        return computer.getOperationsResult()
    }

    fun solvePartTwo(input: List<String>): Long {
        val parsed = parseInput(input)
        val regB = parsed[1] as Long
        val regC = parsed[2] as Long
        val instructions = parsed[3] as List<Instruction>

        val target = instructions.flatMap { listOf(it.opcode, it.operator) }.reversed()

        fun findA(a: Long = 0L, depth: Int = 0): Long {
            if (depth == target.size) {
                return a
            }

            for (i in 0..7) {
                val newA = a * 8 + i
                val computer = ChronospatialComputer(newA, regB, regC, instructions)
                val output = computer.getOperationsResult()

                if (output.isNotEmpty() && output[0] == target[depth]) {
                    val result = findA(newA, depth + 1)
                    if (result != 0L) {
                        return result
                    }
                }
            }
            return 0
        }

        return findA()
    }

    val lines: List<String> = readInput("Day17")

    "---------- Part 1 ----------".println()
    solvePartOne(lines).joinToString(",").println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}