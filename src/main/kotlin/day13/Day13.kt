import java.io.File

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

fun String.findClosingBracketFromPos(openBracketPos: Int): Int {
    var closedBracketPos = openBracketPos
    var counter = 1
    while (counter > 0) {
        when (this[++closedBracketPos]) {
            ']' -> counter--
            '[' -> counter++
        }
    }
    return closedBracketPos
}

fun String.parseLine(): Data {
    if (isEmpty()) return Data.ListData(listOf())

    val list = mutableListOf<Data>()

    var index = 0

    while (index < count()) {
        when (val char = this[index]) {
            '[' -> {
                val closedPos = this.findClosingBracketFromPos(index)
                val sub = substring(startIndex = index + 1, endIndex = closedPos)
                list.add(sub.parseLine())
                index = closedPos
            }

            ',' -> {}
            else -> {
                var digitToParse = ""
                var curr = char
                while (curr.isDigit()) {
                    digitToParse += curr
                    index++
                    if (index == count()) {
                        break
                    }
                    curr = this[index]
                    continue
                }
                list.add(Data.Integer(digitToParse.toInt()))
            }
        }
        index++
    }

    return Data.ListData(list)
}

fun parseInput(input: () -> List<String>): List<Compare> = input().chunked(2).map {
    Compare(
        it.component1().parseLine(),
        it.component2().parseLine()
    )
}

fun parseInput2(input: () -> List<String>): List<Data> = input().map { it.parseLine() }


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

fun Data.compare(second: Data): Result = when {
    this is Data.Integer && second is Data.Integer -> this.compare(second)
    this is Data.ListData && second is Data.ListData -> this.compare(second)
    this is Data.ListData && second is Data.Integer -> this.compare(Data.ListData(listOf(second)))
    this is Data.Integer && second is Data.ListData -> Data.ListData(listOf(this)).compare(second)
    else -> error("No such comparison scenario")
}

fun Result.toInt() = when (this) {
    is Result.CorrectOrder -> 1
    is Result.IncorrectOrder -> -1
    else -> 0
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
        firstIterator.hasNext() && !secondIterator.hasNext() -> Result.IncorrectOrder
        secondIterator.hasNext() && !firstIterator.hasNext() -> Result.CorrectOrder
        else -> Result.Tie
    }
}

fun Compare.eval(): Result = first.compare(second)

fun day131() = parseInput { input }
    .mapIndexed { index, pair ->
        when (pair.eval()) {
            Result.CorrectOrder -> index + 1
            else -> 0
        }
    }.sum()

fun day132() = parseInput2 { input + "[[6]]" + "[[2]]" }.sortedWith { o1, o2 ->
    o2.compare(o1).toInt()
}.foldIndexed(1) { index, acc, data ->
    when (data == "[[6]]".parseLine() || data == "[[2]]".parseLine()) {
        false -> acc
        true -> (index + 1) * acc
    }
}

fun main() {
    println(day131())
    println(day132())
}