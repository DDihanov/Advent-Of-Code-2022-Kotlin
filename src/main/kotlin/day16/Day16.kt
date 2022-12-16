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
fun day161() {
    val allTunnels = parseInput { input }

    // exclude zero flow tunnels
    val targetTunnels = allTunnels.values.filter { it.flowRate > 0 }

    val start = allTunnels["AA"]!!

    val findBest = targetTunnels.maxOf { target ->
        dfs(
            start,
            target,
            0,
            0,
            targetTunnels - target,
            listOf(),
            listOf(start),
            allTunnels
        )
    }

    println(findBest)
}

fun dfs(
    current: Tunnel,
    target: Tunnel,
    currentTime: Int,
    accumulatedPressure: Int,
    targets: List<Tunnel>,
    openValves: List<Tunnel>,
    visited: List<Tunnel>,
    allTunnels: Map<String, Tunnel>
): Int {
    if (currentTime > MAX_TIME) {
        return accumulatedPressure
    }

    return when (current) {
        target -> {
            when {
                targets.isEmpty() -> accumulatedPressure + (MAX_TIME - currentTime) * openValves.sumOf { it.flowRate }
                else -> targets.maxOf { newTarget ->
                    dfs(
                        current,
                        newTarget,
                        currentTime + 1,
                        accumulatedPressure + openValves.sumOf { it.flowRate },
                        targets - newTarget,
                        openValves + current,
                        listOf(current),
                        allTunnels
                    )
                }
            }
        }

        else -> {
            current.leadsTo.maxOf { child: String ->
                val childFromMap = allTunnels[child]!!
                when {
                    visited.contains(childFromMap) -> 0
                    else -> dfs(
                        childFromMap,
                        target,
                        currentTime + 1,
                        accumulatedPressure + openValves.sumOf { it.flowRate },
                        targets,
                        openValves,
                        visited + childFromMap,
                        allTunnels
                    )
                }
            }
        }
    }
}

fun main() {
    day161()
}