package day08

import java.io.File

val input = File("src/main/kotlin/day8/Day8.txt")
fun parseMatrix(input: () -> String, lineLength: Int) = input()
    // input has carriage returns \r and will crash the parser so digitToIntOrNull needed
    .mapNotNull { it.digitToIntOrNull() }
    .toArrayOfIntArrays(lineLength)

fun List<Int>.toArrayOfIntArrays(rows: Int) = this.chunked(rows).map { it.toIntArray() }.toTypedArray()

val matrix = parseMatrix(
    input = {
        input.readText()
    },
    // any element will do as input is always rectangular
    lineLength = input.readLines().first().count()
)

fun day81(): Int {
    fun Array<IntArray>.checkIfVisibleRight(row: Int, col: Int): Boolean {
        if (row == 0 || col == 0) return true

        val startingTree = this[row][col]
        for (i in col + 1 until this[row].size) {
            val toCompare = this[row][i]
            if (toCompare >= startingTree) return false
        }

        return true
    }

    fun Array<IntArray>.checkIfVisibleLeft(row: Int, col: Int): Boolean {
        if (row == 0 || col == 0) return true

        val startingTree = this[row][col]
        for (i in col - 1 downTo 0) {
            val toCompare = this[row][i]
            if (toCompare >= startingTree) return false
        }

        return true
    }

    fun Array<IntArray>.checkIfVisibleDown(row: Int, col: Int): Boolean {
        if (row == 0 || col == 0) return true

        val startingTree = this[row][col]
        for (i in row + 1 until size) {
            val toCompare = this[i][col]
            if (toCompare >= startingTree) return false
        }

        return true
    }

    fun Array<IntArray>.checkIfVisibleUp(row: Int, col: Int): Boolean {
        if (row == 0 || col == 0) return true

        val startingTree = this[row][col]
        for (i in row - 1 downTo 0) {
            val toCompare = this[i][col]
            if (toCompare >= startingTree) return false
        }

        return true
    }

    fun Array<IntArray>.checkIfVisible(row: Int, col: Int) =
        checkIfVisibleUp(row, col) ||
                checkIfVisibleDown(row, col) ||
                checkIfVisibleLeft(row, col) ||
                checkIfVisibleRight(row, col)

    var numVisible = 0
    for (row in matrix.indices) {
        for (col in 0 until matrix[row].size) {
            if (matrix.checkIfVisible(row, col)) numVisible++
        }
    }

    return numVisible
}

fun day82(): Int {
    fun Array<IntArray>.visibilityCountRight(row: Int, col: Int): Int {
        if (col == this[col].size) return 0

        val startingTree = this[row][col]
        var visibleCount = 0
        for (i in col + 1 until this[row].size) {
            val toCompare = this[row][i]
            when {
                startingTree > toCompare -> visibleCount++
                else -> {
                    visibleCount++
                    break
                }
            }
        }

        return visibleCount
    }

    fun Array<IntArray>.visibilityCountLeft(row: Int, col: Int): Int {
        if (col == 0) return 0

        val startingTree = this[row][col]
        var visibleCount = 0
        for (i in col - 1 downTo 0) {
            val toCompare = this[row][i]
            when {
                startingTree > toCompare -> visibleCount++
                else -> {
                    visibleCount++
                    break
                }
            }
        }

        return visibleCount
    }

    fun Array<IntArray>.visibilityCountDown(row: Int, col: Int): Int {
        if (col == size) return 0

        val startingTree = this[row][col]
        var visibleCount = 0
        for (i in row + 1 until size) {
            val toCompare = this[i][col]
            when {
                startingTree > toCompare -> visibleCount++
                else -> {
                    visibleCount++
                    break
                }
            }
        }

        return visibleCount
    }

    fun Array<IntArray>.visibilityCountUp(row: Int, col: Int): Int {
        if (row == 0) return 0

        val startingTree = this[row][col]
        var visibleCount = 0
        for (i in row - 1 downTo 0) {
            val toCompare = this[i][col]
            when {
                startingTree > toCompare -> visibleCount++
                else -> {
                    visibleCount++
                    break
                }
            }
        }

        return visibleCount
    }

    fun Array<IntArray>.positionVisibilityScore(row: Int, col: Int): VisibilityScore = VisibilityScore(
        up = visibilityCountUp(row, col),
        down = visibilityCountDown(row, col),
        left = visibilityCountLeft(row, col),
        right = visibilityCountRight(row, col)
    )

    val listOfVisibilityScores = mutableListOf<VisibilityScore>()
    for (row in matrix.indices) {
        for (col in 0 until matrix[row].size) {
            listOfVisibilityScores.add(matrix.positionVisibilityScore(row, col))
        }
    }

    return listOfVisibilityScores.map {
        it.sum()
    }.maxOf { it }
}

data class VisibilityScore(val up: Int, val down: Int, val left: Int, val right: Int)

fun VisibilityScore.sum() = up * down * left * right

fun main() {
    println(day81())
    println(day82())
}

