package day4

import java.io.File

// splints into e.g. 4-7,6-9
fun parseInput(input: () -> String): List<String> = input().split('\n')

fun parseToPopulatedLists() =
    parseInput { File("src/main/kotlin/day4/Day4.txt").readText() }.map { pair ->
        val (first, second) = pair.split(',')
        val (firstPairStart, firstPairEnd) = first.split('-')
        val firstList = buildList {
            (firstPairStart.toInt()..firstPairEnd.toInt()).forEach {
                add(it)
            }
        }
        val (secondPairStart, secondPairEnd) = second.split('-')
        val secondList = buildList {
            (secondPairStart.toInt()..secondPairEnd.toInt()).forEach {
                add(it)
            }
        }
        firstList to secondList
    }

// contains all
fun day41() = parseToPopulatedLists().sumOf { pair ->
    val firstList = pair.first
    val secondList = pair.second

    when (firstList.containsAll(secondList) or secondList.containsAll(firstList)) {
        true -> 1
        false -> 0
    }.toInt()
}

// contains any
fun day42() =  parseToPopulatedLists().sumOf { pair ->
    val firstList = pair.first
    val secondList = pair.second

    when ((firstList intersect secondList.toSet()).isEmpty()) {
        true -> 0
        false -> 1
    }.toInt()
}

fun main() {
    println(day41())
    println(day42())
}