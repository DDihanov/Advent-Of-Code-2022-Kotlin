package day03

import java.io.File

const val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

val prioritiesMapLower = (1..26).associateBy { alphabet[it - 1].toString() }

val prioritiesMapUpper = (27..52).associateBy { alphabet[it - 1].toString() }

fun parseInputToMap(input: () -> String): List<String> = input()
    .split("\n")

fun day31(): Int = parseInputToMap { File("src/main/kotlin/day3/Day3.txt").readText() }
    .map {
        val firstCompartment = it.substring(startIndex = 0, endIndex = it.count() / 2)
        val secondCompartment = it.substring(startIndex = it.count() / 2, endIndex = it.count())

        firstCompartment.toSet() intersect secondCompartment.toSet()
    }.sumOf { itemList ->
        itemList.sumOf { item ->
            val lowerSum = prioritiesMapLower.getOrDefault(item.toString(), 0)
            val upperSum = prioritiesMapUpper.getOrDefault(item.toString(), 0)
            lowerSum + upperSum
        }
    }

fun day32(): Int = parseInputToMap { File("src/main/kotlin/day3/Day3.txt").readText() }
    .chunked(3)
    .map {
        it[0].toSet() intersect it[1].toSet() intersect it[2].toSet()
    }.sumOf { itemList ->
        itemList.sumOf { item ->
            val lowerSum = prioritiesMapLower.getOrDefault(item.toString(), 0)
            val upperSum = prioritiesMapUpper.getOrDefault(item.toString(), 0)
            lowerSum + upperSum
        }
    }


fun main() {
    println(day31())
    println(day32())
}