fun main() {

    fun checkXMASHorizontally(input: List<CharArray>, row: Int, column: Int): Long {
        return runCatching {
            val candidate = input[row].slice(column until column + 4).joinToString("")
            if (candidate == "XMAS" || candidate.reversed() == "XMAS") {
                1L
            } else {
                0L
            }
        }.getOrDefault(0L)
    }

    fun checkXMASVertically(input: List<CharArray>, row: Int, column: Int): Long {
        return runCatching {
            val candidate = input.map { it[column] }.slice(row until row + 4).joinToString("")
            if (candidate == "XMAS" || candidate.reversed() == "XMAS") {
                1L
            } else {
                0L
            }
        }.getOrDefault(0L)
    }

    fun checkXMASLeftDiagonally(input: List<CharArray>, row: Int, column: Int): Long {
        return runCatching {
            val candidate = buildString {
                for (i in 0 until 4) {
                    append(input[row + i][column - i])
                }
            }
            if (candidate == "XMAS" || candidate.reversed() == "XMAS") {
                1L
            } else {
                0L
            }
        }.getOrDefault(0L)
    }

    fun checkXMASRightDiagonally(input: List<CharArray>, row: Int, column: Int): Long {
        return runCatching {
            val candidate = buildString {
                for (i in 0 until 4) {
                    append(input[row + i][column + i])
                }
            }
            if (candidate == "XMAS" || candidate.reversed() == "XMAS") {
                1L
            } else {
                0L
            }
        }.getOrDefault(0L)
    }

    fun checkXMAS(input: List<CharArray>, row: Int, column: Int): Long {
        return checkXMASHorizontally(input, row, column) +
                checkXMASVertically(input, row, column) +
                checkXMASLeftDiagonally(input, row, column) +
                checkXMASRightDiagonally(input, row, column)
    }

    fun checkX_MAS(input: List<CharArray>, row: Int, column: Int): Long {
        return runCatching {
            val leftSide = buildString {
                for (i in 0 until 3) {
                    append(input[row + i][column + i])
                }
            }
            val rightSide = buildString {
                for (i in 0 until 3) {
                    append(input[row + i][column + 2 - i])
                }
            }
            if ((leftSide == "MAS" || leftSide.reversed() == "MAS") &&
                (rightSide == "MAS" || rightSide.reversed() == "MAS")) {
                1L
            } else {
                0L
            }
        }.getOrDefault(0L)
    }

    fun solvePartOne(input: List<CharArray>): Long {
        return input.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, char ->
                if (char == 'X' || char == 'S') checkXMAS(input, rowIndex, columnIndex) else 0L
            }.sum()
        }.sum()
    }

    fun solvePartTwo(input: List<CharArray>): Long {
        return input.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, char ->
                if (char == 'M' || char == 'S') checkX_MAS(input, rowIndex, columnIndex) else 0L
            }.sum()
        }.sum()
    }

    val lines = readInput("Day04").map { it.toCharArray() }

    "---------- Part 1 ----------".println()
    solvePartOne(lines).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(lines).println()
}