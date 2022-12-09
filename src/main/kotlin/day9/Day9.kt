package day9

import java.io.File
import kotlin.math.hypot

private val input = File("src/main/kotlin/day9/Day9.txt").readLines()

private val moves = parseInput {
    input
}.map { it.toMove() }

fun parseInput(input: () -> List<String>) = input()

data class Node(val x: Int, val y: Int, var next: Node? = null)

fun Node.calcDistance(other: Node) = hypot((other.x - this.x).toDouble(), (other.y - this.y).toDouble()).toInt()

fun Node.moveUp() = this.copy(y = y - 1)

fun Node.moveDown() = this.copy(y = y + 1)

fun Node.moveRight() = this.copy(x = x + 1)

fun Node.moveLeft() = this.copy(x = x - 1)

sealed class Direction {
    sealed class Vertical : Direction() {
        object Up : Vertical()
        object Down : Vertical()
    }

    sealed class Horizontal : Direction() {
        object Left : Horizontal()

        object Right : Horizontal()
    }
}

fun String.toDirection() = when (this) {
    "U" -> Direction.Vertical.Up
    "D" -> Direction.Vertical.Down
    "L" -> Direction.Horizontal.Left
    "R" -> Direction.Horizontal.Right
    else -> error("Invalid direction")
}

fun Node.move(direction: Direction): Node = run {
    val newCurrent = when (direction) {
        Direction.Horizontal.Left -> moveLeft()
        Direction.Horizontal.Right -> moveRight()
        Direction.Vertical.Down -> moveDown()
        Direction.Vertical.Up -> moveUp()
    }
    // after a move sync children
    newCurrent.link(newCurrent.next?.sync(newCurrent))
    newCurrent
}

fun Node.sync(with: Node): Node {
    // if positions are the same no sync necessary
    if (this == with) return this

    val new = when (with.calcDistance(this)) {
        0, 1 -> this
        else -> {
            when {
                (with.x > this.x) -> moveRight()
                (with.x < this.x) -> moveLeft()
                else -> this
            }.run {
                when {
                    (with.y < this.y) -> moveUp()
                    (with.y > this.y) -> moveDown()
                    else -> this
                }
            }
        }
    }

    // current node has moved, recursively sync all children nodes
    new.link(new.next?.sync(new))

    return new
}

const val STARTING_X = 0
const val STARTING_Y = 0

private fun String.toMove() = split(" ").run { Move(component1().toDirection(), component2().toInt()) }

private fun Node.play(moves: List<Move>): Int {
    var head = this
    val uniquePositionSet = mutableSetOf<Node>()

    moves.forEach { move ->
        repeat(move.count) {
            head = head.move(move.direction).also {
                uniquePositionSet.add(head.last())
            }
        }
    }

    return uniquePositionSet.count()
}

fun Node.last(): Node = when (val right = this.next) {
    null -> this
    else -> right.last()
}

private data class Move(val direction: Direction, val count: Int)

fun Node.link(next: Node?) {
    this.next = next
}

fun main() {
    val day91head = Node(STARTING_X, STARTING_Y).apply {
        link(Node(STARTING_X, STARTING_Y))
    }
    println(day91head.play(moves))
    val day92head = Node(STARTING_X, STARTING_Y).apply {
        repeat(9) {
            last().link(Node(STARTING_X, STARTING_Y))
        }
    }
    println(day92head.play(moves))
}