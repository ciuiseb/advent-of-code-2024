import utils.println
import utils.readInput

data class File(val id: String, val size: Long)

fun main() {
    fun String.getDiskMapRepresentation(): MutableList<String> =
        buildList {
            generateSequence(0) { it + 1 }
                .takeWhile { it < length }
                .forEach { index ->
                    val currentNumber = this@getDiskMapRepresentation[index].digitToInt()
                    when {
                        index % 2 == 0 -> {
                            repeat(currentNumber) {
                                add((index / 2).toString())
                            }
                        }

                        else -> {
                            repeat(currentNumber) {
                                add(".")
                            }
                        }
                    }
                }
        }.toMutableList()

    fun String.getDiskMapRepresentationWithFiles(): MutableList<File> =
        buildList {
            generateSequence(0) { it + 1 }
                .takeWhile { it < length }
                .forEach { index ->
                    val currentNumber = this@getDiskMapRepresentationWithFiles[index].digitToInt()
                    when {
                        index % 2 == 0 -> add(File((index / 2).toString(), currentNumber.toLong()))
                        else -> add(File(".", currentNumber.toLong()))
                    }
                }
        }.toMutableList()


    fun List<String>.checkSum(): Long = indices
        .sumOf { index ->
            if (this[index] == ".") 0L else
                (this[index].toLong() * index.toLong())
        }

    fun MutableList<String>.compressDiskMapWithFileFragmentation(): MutableList<String> {
        var firstFreeSpace = 0
        var lastFile = size - 1
        while (firstFreeSpace <= lastFile) {
            while (this[firstFreeSpace] != "." && firstFreeSpace in indices) firstFreeSpace++
            while (this[lastFile] == "." && lastFile in indices) lastFile--
            if (firstFreeSpace < lastFile) {
                this[firstFreeSpace] = this[lastFile]
                    .also { this[lastFile] = this[firstFreeSpace] }
                firstFreeSpace++
                lastFile--
            }
        }
        return this
    }

    fun MutableList<File>.toStringList(): List<String> = buildList {
        this@toStringList.forEach { file ->
            repeat(file.size.toInt()) {
                add(file.id)
            }
        }
    }

    fun MutableList<File>.compressDiskMap(): List<String> {
        var lastFile = size - 1
        while (lastFile in indices) {
            while (this[lastFile].id == "." && lastFile in indices) lastFile--
            for (firstFreeSpace in indices) {
                if (this[firstFreeSpace].id == "."
                    && this[firstFreeSpace].size >= this[lastFile].size
                    && firstFreeSpace < lastFile
                ) {
                    val sizeDifference = this[firstFreeSpace].size - this[lastFile].size
                    val fileToMove = File(this[lastFile].id, this[lastFile].size)
                    val freeSpaceToMove = File(".", this[lastFile].size)

                    if(sizeDifference == 0L) {
                        this[firstFreeSpace] = fileToMove
                        this[lastFile] = freeSpaceToMove
                    } else {
                        add(firstFreeSpace, fileToMove)
                        this[firstFreeSpace + 1] = File(".", sizeDifference)
                        this[++lastFile] = freeSpaceToMove
                    }

                    break
                }
            }
            lastFile--
        }
        return toStringList()
    }

    fun solvePartOne(diskMapRepresentation: MutableList<String>): Long =
        diskMapRepresentation.compressDiskMapWithFileFragmentation().checkSum()

    fun solvePartTwo(diskMapRepresentation: MutableList<File>): Long =
        diskMapRepresentation.compressDiskMap().checkSum()

    val line = readInput("Day09").first()

    "---------- Part 1 ----------".println()
    solvePartOne(line.getDiskMapRepresentation()).println()
    "---------- Part 2 ----------".println()
    solvePartTwo(line.getDiskMapRepresentationWithFiles()).println()
}