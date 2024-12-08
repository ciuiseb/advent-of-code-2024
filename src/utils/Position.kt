package utils

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position): Position = Position(this.x + other.x, this.y + other.y)
    operator fun minus(other: Position): Position = Position(this.x - other.x, this.y - other.y)
    operator fun times(other: Position) = Position(x * other.x, y * other.y)
    operator fun times(scale: Int) = Position(x * scale, y * scale)
}