package day10

import java.io.File


private val input = File("src/main/kotlin/day10/Day10.txt").readLines()

private sealed class Command {
    object Noop : Command()
    data class AddX(val value: Int) : Command()
}

private fun String.toCommand(): Command {
    val split = this.split(" ")
    return when {
        split.component1() == "noop" -> Command.Noop
        else -> Command.AddX(split.component2().toInt())
    }
}

private fun parseInput(input: () -> List<String>) = input().map { it.toCommand() }

fun day101(): Int {
    fun List<Int>.sum(cycle: Int, registerValue: Int) = this.contains(cycle).run {
        when (this) {
            true -> cycle.signalStrength(registerValue)
            false -> 0
        }
    }

    val input = parseInput { input }

    var cycle = 0
    var registerValue = 1

    var cycleSum = 0
    val toRegister = listOf(20, 60, 100, 140, 180, 220)

    input.forEach { command ->
        when (command) {
            is Command.AddX -> {
                repeat(2) { time ->
                    cycle++
                    cycleSum += toRegister.sum(cycle = cycle, registerValue = registerValue)
                    if (time == 1) registerValue += command.value
                }
            }

            Command.Noop -> {
                cycle++
                cycleSum += toRegister.sum(cycle = cycle, registerValue = registerValue)
            }
        }
    }

    return cycleSum
}

fun day102() {
    fun checkAndDraw(spritePos: Int, screenPos: Int) {
        when (spritePos - 1 == screenPos || spritePos + 1 == screenPos || spritePos == screenPos) {
            true -> print("#")
            false -> print(".")
        }
        if (screenPos == 39) {
            println()
        }
    }

    val input = parseInput { input }

    var crtPos = 0
    var spritePos = 1

    input.forEach { command ->
        when (command) {
            is Command.AddX -> {
                repeat(2) { time ->
                    checkAndDraw(spritePos, crtPos % 40)
                    crtPos++
                    // time == 1 means this is the second cycle
                    if (time == 1) spritePos += command.value
                }
            }

            Command.Noop -> {
                checkAndDraw(spritePos, crtPos % 40)
                crtPos++
            }
        }
    }
}


//calc signal strength cycle * register value
private fun Int.signalStrength(registerValue: Int) = this * registerValue

fun main() {
    println(day101())
    day102()
}
