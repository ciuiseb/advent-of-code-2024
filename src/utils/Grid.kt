package utils

open class Grid<T> (val map: MutableList<MutableList<T>>) {
    open fun get(position: Position): T = map[position.x][position.y]
    open fun isInBounds(position: Position): Boolean = position.x in map.indices && position.y in map[0].indices
}