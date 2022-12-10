package day05

import java.io.File

fun parseStacks(input: () -> List<String>) = input().let {
    // all stacks without last line
    val numOfStacks = parseIndexLine { it.last() }.maxOf { it.toString().toInt() }
    // list of stacks with a key representing the column
    val stacks = buildMap {
        repeat(numOfStacks) { index ->
            put(index + 1, mutableListOf<String>())
        }
    }

    val onlyStackLines = input().dropLast(1)

    // keep track of the column after every pass
    var column = 1
    // iterate over the string knowing that every
    // 2nd character is either a letter or an empty space representing the column value
    onlyStackLines.forEachIndexed { _: Int, string: String ->
        var charPosition = 1
        while (charPosition < string.count()) {
            val columnValue = string[charPosition]
            val stackInMap = stacks[column]
            if (columnValue != ' ') {
                stackInMap?.add(columnValue.toString())
            }
            charPosition += 4
            column++
        }
        // reset it after a pass
        column = 1
    }

    stacks
}

fun parseIndexLine(line: () -> String) = line().replace(" ", "")

data class Instruction(val amount: Int, val from: Int, val to: Int)

val instructions = File("src/main/kotlin/day5/Day5_moves.txt").readLines().map { it.split(' ') }.map {
    Instruction(amount = it[1].toInt(), from = it[3].toInt(), to = it[5].toInt())
}

fun day51() = parseStacks { File("src/main/kotlin/day5/Day5_stacks.txt").readLines() }.let { stacks ->
    instructions.forEach {
        val origin = stacks[it.from]!!
        val destination = stacks[it.to]!!
        repeat(it.amount) {
            val element = origin.removeFirst()
            destination.add(0, element)
        }
    }
    stacks
}.topmostValues()

fun day52() = parseStacks { File("src/main/kotlin/day5/Day5_stacks.txt").readLines() }.let { stacks ->
    instructions.forEach {
        val origin = stacks[it.from]!!
        val destination = stacks[it.to]!!
        val toAdd = mutableListOf<String>()
        repeat(it.amount) {
            val element = origin.removeFirst()
            //add at the end to "preserve order"
            toAdd.add(toAdd.count(), element)
        }
        destination.addAll(0, toAdd)
    }
    stacks
}.topmostValues()

private fun Map<Int, MutableList<String>>.topmostValues(): String {
    val topMostItems = buildString {
        values.forEach {
            append(it.first())
        }
    }
    return topMostItems
}

fun main() {
    println(day51())
    println(day52())
}