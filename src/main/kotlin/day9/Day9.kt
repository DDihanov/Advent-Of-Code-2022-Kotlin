package day9

import day2.toMove
import java.io.File
import kotlin.math.hypot

val input = File("src/main/kotlin/day9/Day9.txt").readLines()

fun parseInput(input: () -> List<String>) = input()
data class Point(val x: Int, val y: Int)

fun Point.calcDistance(other: Point) = hypot((other.x - this.x).toDouble(), (other.y - this.y).toDouble()).toInt()

fun Point.moveUp() = this.copy(y = y - 1)

fun Point.moveDown() = this.copy(y = y + 1)

fun Point.moveRight() = this.copy(x = x + 1)

fun Point.moveLeft() = this.copy(x = x - 1)

fun Point.syncX(other: Point) = this.copy(x = other.x)

fun Point.syncY(other: Point) = this.copy(y = other.y)

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


data class NewPosition(val head: Point, val tail: Point)

fun Direction.Vertical.moveHead(head: Point, tail: Point) = run {
    val newHead = when (this) {
        Direction.Vertical.Up -> head.moveUp()
        Direction.Vertical.Down -> head.moveDown()
    }
    val distance = newHead.calcDistance(tail)
    val newTail = when (distance) {
        0, 1 -> tail
        else -> when (this) {
            Direction.Vertical.Up -> tail.moveUp()
            Direction.Vertical.Down -> tail.moveDown()
        }
    }.run {
        when {
            distance >= 2 -> {
                this.syncX(newHead)
            }

            else -> {
                this
            }
        }
    }
    NewPosition(head = newHead, tail = newTail)
}

fun Direction.Horizontal.moveHead(head: Point, tail: Point) = run {
    val newHead = when (this) {
        Direction.Horizontal.Left -> head.moveLeft()
        Direction.Horizontal.Right -> head.moveRight()
    }
    val distance = newHead.calcDistance(tail)
    val newTail = when (distance) {
        0, 1 -> tail
        else -> when (this) {
            Direction.Horizontal.Left -> tail.moveLeft()
            Direction.Horizontal.Right -> tail.moveRight()
        }
    }.run {
        when {
            distance >= 2 -> {
                syncY(newHead)
            }

            else -> {
                this
            }
        }
    }
    NewPosition(head = newHead, tail = newTail)
}

fun Direction.moveHead(head: Point, tail: Point) = when (this) {
    Direction.Horizontal.Left -> (this as Direction.Horizontal.Left).moveHead(head, tail)
    Direction.Horizontal.Right -> (this as Direction.Horizontal.Right).moveHead(head, tail)
    Direction.Vertical.Down -> (this as Direction.Vertical.Down).moveHead(head, tail)
    Direction.Vertical.Up -> (this as Direction.Vertical.Up).moveHead(head, tail)
}

const val STARTING_X = 0
const val STARTING_Y = 4

fun String.toMoveDirectionPair() = split(" ").run { component1().toDirection() to component2().toInt() }
fun day91(): Int {
    val moves = parseInput {
        input
    }.map { it.toMoveDirectionPair() }

    var head = Point(STARTING_X, STARTING_Y)
    var tail = Point(STARTING_X, STARTING_Y)
    val uniquePositionSet = mutableSetOf<Point>()

    moves.forEach { command ->
        val (direction, moveCount) = command.first to command.second
        repeat(moveCount) {
            direction.moveHead(head, tail).apply {
                head = this.head
                tail = this.tail
                uniquePositionSet.add(tail)
            }
        }
    }

    return uniquePositionSet.count()
}

fun main() {
    println(day91())
}