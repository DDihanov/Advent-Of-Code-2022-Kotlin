package day16

import java.io.File


data class Tunnel(val valve: String, val flowRate: Int, val leadsTo: List<String>)

val regex = "Valve ([A-Z][A-Z]) has flow rate=(\\d+); tunnel(s?) lead(s?) to valve(s?) (.+)".toRegex()

fun String.parse() = regex.findAll(this).flatMap {
    it.groupValues.subList(1, it.groupValues.count())
}.toList()

val input = File("src/main/kotlin/day16/Day16.txt").readLines()

fun parseInput(input: () -> List<String>) = input().associate { line ->
    val data = line.parse()
    val leadsTo = data[5].split(", ")
    data[0] to Tunnel(valve = data[0], flowRate = data[1].toInt(), leadsTo = leadsTo)
}

const val MAX_TIME = 30

data class State(val currentLocation: Tunnel, val valves: List<Tunnel>, val time: Int)

fun day161(): Int {
    val allTunnels = parseInput { input }

    val start = allTunnels["AA"]!!

    // state and score for given state
    val dp = mutableMapOf<State, Int>()

    return dfsWithMemoization(
        start,
        0,
        0,
        listOf(),
        allTunnels,
        dp
    )
}

fun dfsWithMemoization(
    current: Tunnel,
    currentTime: Int,
    accumulatedPressure: Int,
    openValves: List<Tunnel>,
    allTunnels: Map<String, Tunnel>,
    states: MutableMap<State, Int>
): Int {
    if (currentTime == MAX_TIME) {
        return accumulatedPressure
    }

    val key = State(current, openValves, currentTime)

    when (val state = states[key]) {
        null -> {}
        else -> return state
    }

    val newBest = when {
        current.flowRate > 0 && !openValves.contains(current) -> dfsWithMemoization(
            current,
            currentTime + 1,
            accumulatedPressure + openValves.sumOf { it.flowRate },
            openValves + current,
            allTunnels,
            states
        )

        else -> current.leadsTo.maxOf { child: String ->
            val childFromMap = allTunnels[child]!!
            dfsWithMemoization(
                childFromMap,
                currentTime + 1,
                accumulatedPressure + openValves.sumOf { it.flowRate },
                openValves,
                allTunnels,
                states
            )
        }
    }

    states[key] = newBest

    return newBest
}

fun main() {
    println(day161())
}