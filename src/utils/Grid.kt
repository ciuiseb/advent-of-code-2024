package utils

data class Grid<T> (val map: List<List<T>>) {
    fun get(position: Position): T = map[position.x][position.y]
    fun isInBounds(position: Position): Boolean = position.x in map.indices && position.y in map[0].indices
}