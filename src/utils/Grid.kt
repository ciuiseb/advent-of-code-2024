package utils

data class Grid(val map: List<List<Int>>) {
    fun getNeighbours(position: Position): List<Position> = buildList {
        TrailDirections.entries.forEach {
            val neighbourPosition = position + it.position

            if (isInBounds(neighbourPosition)) add(neighbourPosition)
        }
    }

    private fun isInBounds(position: Position): Boolean = position.x in map.indices && position.y in map[0].indices
}