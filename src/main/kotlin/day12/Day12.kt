package day12

import java.io.File

// start and end node have default char values of 'a' and 'z' respectively
sealed class Node {
    data class Start(val x: Int, val y: Int, val elev: Char = 'a', val visited: Boolean = false) : Node()
    data class End(val x: Int, val y: Int, val elev: Char = 'z', val visited: Boolean = false) : Node()
    data class Other(val x: Int, val y: Int, val elev: Char, val visited: Boolean = false) : Node()
}

val input = File("src/main/kotlin/day12/Day12.txt").readLines()

data class Grid(val start: Node.Start, val end: Node.End, val nodes: List<List<Node>>)

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

fun day121() {
    val input = parseInput { input }

    println()
}

fun main() {
    day121()
}