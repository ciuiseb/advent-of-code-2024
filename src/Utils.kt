import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("resources/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


fun String.parseNumbers(separator: String): List<Int> = this.split(separator).map(String::toInt)

fun <T> List<T>.middle(): T = get(size/2)

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position): Position = Position(this.x + other.x, this.y + other.y)
}

val INVALID_POSITION = Position(-1, -1)

fun <T> List<List<T>>.get(position: Position): T = this[position.x][position.y]
fun <T> List<MutableList<T>>.set(position: Position, newValue: T) {
    this[position.x][position.y] = newValue
}