package day02

import java.io.File

const val OPPONENT_ROCK = "A"
const val OPPONENT_PAPER = "B"
const val OPPONENT_SCISSOR = "C"

const val MY_ROCK = "X"
const val MY_PAPER = "Y"
const val MY_SCISSOR = "Z"

fun String.toMove() = when (this) {
    OPPONENT_PAPER, MY_PAPER -> Move.Paper
    OPPONENT_ROCK, MY_ROCK -> Move.Rock
    OPPONENT_SCISSOR, MY_SCISSOR -> Move.Scissor
    else -> error("No such value")
}

fun String.toGameResult() = when (this) {
    MY_ROCK -> Result.Loss
    MY_PAPER -> Result.Draw
    else -> Result.Win
}

sealed class Move(val scoreValue: Int) {
    object Rock : Move(1)
    object Paper : Move(2)
    object Scissor : Move(3)
}

fun Move.beats(otherMove: Move) = when (this) {
    Move.Paper -> otherMove is Move.Rock
    Move.Rock -> otherMove is Move.Scissor
    Move.Scissor -> otherMove is Move.Paper
}

fun Move.moveFromGame(gameResult: Result): Move = when (gameResult) {
    Result.Draw -> this
    Result.Loss -> when (this) {
        Move.Paper -> Move.Rock
        Move.Rock -> Move.Scissor
        Move.Scissor -> Move.Paper
    }
    Result.Win -> when (this) {
        Move.Paper -> Move.Scissor
        Move.Rock -> Move.Paper
        Move.Scissor -> Move.Rock
    }
}

data class Round(val opponent: Move, val me: Move)

data class RiggedRound(val opponent: Move, val outcome: Result)

sealed class Result(val score: Int) {
    object Win : Result(6)
    object Draw : Result(3)
    object Loss : Result(0)
}

fun score(opponent: Move, me: Move): Int = when {
    opponent == me -> Result.Draw.score
    me.beats(opponent) -> Result.Win.score
    else -> Result.Loss.score
} + me.scoreValue

fun Result.riggedScore(opponent: Move): Int =
    opponent.moveFromGame(this).scoreValue + this.score

fun parseInput(input: () -> String): List<String> = input()
    .split("\n")

fun day21(): Int = parseInput { File("src/main/kotlin/day2/Day2.txt").readText() }
    .map {
        val round = it.split(' ')
        val opponent = round[0].toMove()
        val me = round[1].toMove()
        Round(opponent, me)
    }
    .map {
        score(it.opponent, it.me)
    }.sumOf { it }

fun day22(): Int = parseInput { File("src/main/kotlin/day2/Day2.txt").readText() }
    .map {
        val round = it.split(' ')
        val opponent = round[0].toMove()
        val second = round[1].toGameResult()
        RiggedRound(opponent, second)
    }
    .map {
        it.outcome.riggedScore(it.opponent)
    }.sumOf { it }

fun main() {
    println(day21())
    println(day22())
}