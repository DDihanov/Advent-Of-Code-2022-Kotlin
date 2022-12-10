package day01

import java.io.File

fun parseInputToMap(input: () -> String): Map<Int, Int> = input()
    .split("\n\n")
    .withIndex().associate {
        val key = it.index
        val value = it.value.split("\n").sumOf { calorie -> calorie.toInt() }
        key to value
    }

fun day11(): Int = parseInputToMap { File("src/main/kotlin/day1/Day1.txt").readText() }.values.maxOrNull() ?: -1

fun day12(): Int =
    parseInputToMap { File("src/main/kotlin/day1/Day1.txt").readText() }.values.sortedDescending().take(3).sum()

fun main() {
    println(day11())
    println(day12())
}