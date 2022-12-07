package day6

import java.io.File

val input = File("src/main/kotlin/day6/Day6.txt").readText()

fun parseInput(input: () -> String) = input()

fun String.isUniqueCharacters(): Boolean = this.toCharArray().distinct().count() == this.count()

fun day61() = parseInput { input }.let {
    val seq = firstUnique(4, it)
    it.indexOf(seq) + seq.count()
}

fun day62() = parseInput { input }.let {
    val seq = firstUnique(14, it)
    it.indexOf(seq) + seq.count()
}

private fun firstUnique(chunkSize: Int, it: String) = it.windowed(chunkSize, 1).first(String::isUniqueCharacters)

fun main() {
    println(day61())
    println(day62())
}