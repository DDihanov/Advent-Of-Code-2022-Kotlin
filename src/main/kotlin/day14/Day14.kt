package day14

import java.io.File
import kotlin.math.max

val input = File("src/main/kotlin/day14/Day14.txt").readLines().map { it.split(" -> ") }
    .map { inner ->
        inner.map { element -> element.toCoordinates() }
    }

fun String.toCoordinates() = split(",").let { Coordinates(x = it.component1().toInt(), y = it.component2().toInt()) }
data class Coordinates(val x: Int, val y: Int)

fun List<List<Coordinates>>.parseInput(): Set<Coordinates> =
    fold(mutableSetOf()) { acc: MutableSet<Coordinates>, coordinates: List<Coordinates> ->
        coordinates.windowed(2, 1) { pair ->
            val first = pair.component1()
            val second = pair.component2()
            val rockCoordinates = mutableSetOf<Coordinates>()
            var x = first.x
            var y = first.y
            // add starting coordinates always
            rockCoordinates.add(first)
            rockCoordinates.add(second)
            // find direction to draw line to
            val stepX = when {
                first.x - second.x >= 0 -> -1
                else -> 1
            }
            val stepY = when {
                first.y - second.y >= 0 -> -1
                else -> 1
            }
            // draw lines where needed
            while (x != second.x) {
                rockCoordinates.add(Coordinates(x, y))
                x += stepX
            }
            while (y != second.y) {
                rockCoordinates.add(Coordinates(x, y))
                y += stepY
            }
            acc.addAll(rockCoordinates)
        }
        acc
    }

// by order of priority
// down, down-left, down-right
val directions = listOf(
    Coordinates(0, 1),
    Coordinates(-1, 1),
    Coordinates(1, 1)
)

operator fun Coordinates.plus(other: Coordinates) = Coordinates(this.x + other.x, this.y + other.y)

// attempt to move to any direction
// if all the coordinates are taken, then return this position
fun Coordinates.move(rocks: Set<Coordinates>, sand: List<Coordinates>): Coordinates {
    var newPos = this
    val lowestRock = rocks.maxByOrNull { it.y }!!
    val lowestSandFlake = sand.maxByOrNull { it.y } ?: lowestRock
    val lowestPoint = max(lowestRock.y, lowestSandFlake.y)
    while (true) {
        val toMove = directions.map {
            newPos + it
        }.firstOrNull { !rocks.contains(it) && !sand.contains(it) }
        when {
            toMove == null -> break
            // if sand flake "falls through" then return the original starting one
            toMove.y >= lowestPoint -> return this
            else -> newPos = toMove
        }
    }
    return newPos
}

fun day141(): Int {
    val rocks = input.parseInput()
    val sandCoordinates = mutableListOf<Coordinates>()
    val start = Coordinates(500, 0)
    var sandFlake = start.move(rocks, sandCoordinates)
    while (start != sandFlake) {
        sandCoordinates.add(sandFlake)
        sandFlake = start.move(rocks, sandCoordinates)
    }

    return sandCoordinates.count()
}

fun main() {
    println(day141())
}