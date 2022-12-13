package day13

import java.io.File
import kotlin.math.abs

val input = File("src/main/kotlin/day13/Day13.txt").readLines().mapNotNull {
    when (it) {
        "" -> null
        else -> it
    }
}

sealed class Data {
    data class Integer(val value: Int) : Data()

    data class ListData(val data: List<Data> = listOf()) : Data()
}

data class Compare(val first: Data, val second: Data)

fun String.parseList(): List<Data> = this.split(",", limit = 2).fold(listOf()) { acc: List<Data>, s: String ->
    acc + s.parseList()
}

fun String.parseElement(): Data = when {
    this.toIntOrNull() != null -> Data.Integer(this.toInt())
    this == "" -> Data.ListData(listOf())
    else -> Data.ListData(
        this.substringAfter("[")
            .substringBefore("]")
            .parseList()
    )
}

fun parseInput(input: () -> List<String>): List<Compare> = input().chunked(2).map {
    Compare(it.component1().parseElement(), it.component2().parseElement())
}

sealed class Result {
    object CorrectOrder : Result()
    object IncorrectOrder : Result()
    object Tie : Result()
}

fun Data.Integer.compare(other: Data.Integer) = when {
    this.value < other.value -> Result.CorrectOrder
    this.value > other.value -> Result.IncorrectOrder
    else -> Result.Tie
}

fun Data.ListData.compare(other: Data.ListData): Result {
    val firstIterator = this.data.iterator()
    val secondIterator = other.data.iterator()

    while (firstIterator.hasNext() && secondIterator.hasNext()) {
        val firstNext = firstIterator.next()
        val secondNext = secondIterator.next()
        when (val result = Compare(firstNext, secondNext).eval()) {
            Result.Tie -> continue
            else -> return result
        }
    }

    // in case one ran out of items first we need to check which one it was
    return when {
        firstIterator.hasNext() -> Result.CorrectOrder
        else -> Result.IncorrectOrder
    }
}

fun Compare.eval(): Result = when {
    first is Data.Integer && second is Data.Integer -> first.compare(second)
    first is Data.ListData && second is Data.ListData -> first.compare(second)
    first is Data.ListData && second is Data.Integer -> first.compare(Data.ListData(listOf(second)))
    first is Data.Integer && second is Data.ListData -> second.compare(Data.ListData(listOf(first)))
    else -> error("No such comparison scenario")
}

fun day131() = parseInput { input }
    .mapIndexed { index, pair ->
        val result = pair.eval()
        println("pair ${index + 1} result $result")
        result
    }
    .foldIndexed(0) { index, acc, result ->
        when (result) {
            Result.CorrectOrder -> acc + index + 1
            else -> acc + 0
        }
    }

fun main() {
    println(day131())
}