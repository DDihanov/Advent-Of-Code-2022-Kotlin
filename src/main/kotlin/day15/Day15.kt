package day15

import java.io.File
import kotlin.math.abs


val input = File("src/main/kotlin/day15/Day15.txt").readLines()

data class Coordinates(val x: Int, val y: Int)
data class Sensor(val coordinates: Coordinates, val radius: Int)

data class Beacon(val coordinates: Coordinates)

//also the radius of a "diamond"
fun Coordinates.manhattanDistance(other: Coordinates) =
    abs(x - other.x) + abs(y - other.y)

fun String.extractCoordinates() =
    "(-?\\d+)".toRegex().findAll(this).map { it.groupValues[1] }.map { it.toInt() }.toList()

// list of sensors and set of points
fun parseInput(input: () -> List<String>) =
    input().fold(Pair(listOf(), setOf())) { acc: Pair<List<Sensor>, Set<Beacon>>, s: String ->
        val parsedCoordinates = s.extractCoordinates()
        val (sensorCoords, beaconCoords) = Pair(
            Coordinates(
                parsedCoordinates[0],
                parsedCoordinates[1]
            ), Coordinates(parsedCoordinates[2], parsedCoordinates[3])
        )
        Pair(
            acc.first + Sensor(sensorCoords, sensorCoords.manhattanDistance(beaconCoords)),
            acc.second + Beacon(beaconCoords)
        )
    }

fun day151(): Int {
    val (sensors, beacons) = parseInput { input }

    val rowToCheck = 2000000
    val positionsWithoutBeacon = mutableSetOf<Coordinates>()
    // filter all sensors that are in range of the row to check
    // and check their range against the beacons in range
    sensors.filter { it.radius >= it.coordinates.y - rowToCheck }.forEach { sensor ->
        val distanceToCenter = abs(sensor.coordinates.y - rowToCheck)
        val leftmostPointForRow = sensor.coordinates.x - sensor.radius + distanceToCenter
        val rightmostPointForRow = sensor.coordinates.x + sensor.radius - distanceToCenter
        // iterate beacons and check if any are in range
        (leftmostPointForRow..rightmostPointForRow).forEach { x ->
            val toAdd = Coordinates(x, rowToCheck)
            if (beacons.none { it.coordinates.x == x && it.coordinates.y == rowToCheck }) {
                positionsWithoutBeacon.add(toAdd)
            }
        }
    }

    return positionsWithoutBeacon.count()
}

fun day152() {
    val (sensors) = parseInput { input }
    val multiply = 4_000_000
    val maxXY = 4_000_000

    // took about 40 minutes for this to work so be wary :D
    for (sensor in sensors) {
        Thread {
            val leftX = sensor.coordinates.x - sensor.radius - 1
            val rightX = sensor.coordinates.x + sensor.radius + 1
            val topY = sensor.coordinates.y - sensor.radius - 1
            val bottomY = sensor.coordinates.y + sensor.radius + 1
            for (y in topY..bottomY) {
                for (x in leftX..rightX) {
                    if (x < 0 || x > maxXY || y < 0 || y > maxXY) {
                        continue
                    }
                    val outsideCoord = Coordinates(x = x, y = y)
                    when {
                        sensors.none {
                            it.radius >= it.coordinates.manhattanDistance(outsideCoord)
                        } -> {
                            println(outsideCoord.run {
                                (this.x.toBigInteger() * multiply.toBigInteger()) + this.y.toBigInteger()
                            })
                            return@Thread
                        }

                        else -> continue
                    }
                }
            }
        }.start()
    }
}

fun main() {
    println(day151())
    day152()
}