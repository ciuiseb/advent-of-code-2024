package utils

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

/**
 * Converts a string to a list of numbers
 */
fun String.parseNumbers(separator: String): List<Int> = this.split(separator).map(String::toInt)

/**
 * Gets the middle element of a list
 */
fun <T> List<T>.middle(): T = get(size/2)

/**
 * Gets an element from a matrix based on Position
 */
fun <T> List<List<T>>.get(position: Position): T = this[position.x][position.y]
/**
 * Sets an element of a matrix based on Position
 */
fun <T> List<MutableList<T>>.set(position: Position, newValue: T) {
    this[position.x][position.y] = newValue
}

fun String.substringBetween(after: String, before: String) = substringAfter(after).substringBefore(before)
