package day12

import java.io.File

// start and end node have default char values of 'a' and 'z' respectively
sealed class Node(open val x: Int, open val y: Int, open val elev: Char) {
    data class Start(
        override val x: Int,
        override val y: Int,
        override val elev: Char = 'a'
    ) :
        Node(x, y, elev)

    data class End(
        override val x: Int,
        override val y: Int,
        override val elev: Char = 'z'
    ) :
        Node(x, y, elev)

    data class Other(
        override val x: Int,
        override val y: Int,
        override val elev: Char
    ) :
        Node(x, y, elev)
}

val input = File("src/main/kotlin/day12/Day12.txt").readLines()

data class Grid(val start: Node, val end: Node, val nodes: List<List<Node>>)

fun parseInput(input: () -> List<String>): Grid = input().mapIndexed { y, row ->
    row.mapIndexed { x, char ->
        when (char) {
            'S' -> Node.Start(x = x, y = y)
            'E' -> Node.End(x = x, y = y)
            else -> Node.Other(x = x, y = y, elev = char)
        }
    }
}.fold(Grid(Node.Start(-1, -1), Node.End(-1, -1), emptyList())) { acc: Grid, nodes: List<Node> ->
    val start: Node.Start = (nodes.firstOrNull { it is Node.Start } ?: acc.start) as Node.Start
    val end: Node.End = (nodes.firstOrNull { it is Node.End } ?: acc.end) as Node.End
    acc.copy(start = start, end = end, nodes = acc.nodes + listOf(nodes))
}

sealed class Direction(val x: Int, val y: Int) {
    object Up : Direction(0, -1)
    object Down : Direction(0, 1)
    object Left : Direction(-1, 0)
    object Right : Direction(1, 0)
}

val directions = listOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right)

fun day121() = parseInput { input }.bfs().count() - 1

fun day122(): Int = parseInput { input }.let { grid ->
    grid.nodes
        .flatMap { it.filter { node -> node.elev == 'a' } }
        .map { grid.copy(start = it).bfs().count() - 1 }
        .filter { it != 0 }.minOf { it }
}

// returns the shortest path
fun Grid.bfs(): List<Node> {
    val rows = nodes.count()
    val cols = nodes.first().count()

    val path = mutableMapOf<Node, Node>()
    val queue = mutableListOf<Node>()
    val visited = mutableMapOf<Node, Boolean>()
    queue.add(start)
    visited[start] = true

    while (queue.size > 0) {
        val curr = queue.removeLast()
        // short circuit if we find end earlier
        if (curr == end) break
        directions.mapNotNull { direction ->
            val newX = curr.x + direction.x
            val newY = curr.y + direction.y
            when {
                newX < 0 || newY < 0 -> null
                newY >= rows || newX >= cols -> null
                else -> nodes[newY][newX]
            }
        }.filter { current ->
            !visited.getOrDefault(current, false)
        }.filter { node ->
            curr.isValid(node)
        }.forEach { next ->
            queue.add(0, next)
            path[next] = curr
            visited[next] = true
        }
    }

    return path.regeneratePath(end)
}

fun Map<Node, Node>.regeneratePath(tail: Node): List<Node> {
    return when (val next = this[tail]) {
        null -> return listOf<Node>() + tail
        else -> regeneratePath(next) + tail
    }
}

fun Node.isValid(other: Node) = this.elev + 1 >= other.elev

fun main() {
    println(day121())
    println(day122())
}