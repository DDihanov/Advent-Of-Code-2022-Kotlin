package day11

import java.io.File

private val input = File("src/main/kotlin/day11/Day11.txt").readText()

private sealed class Op {
    data class Add(val amount: AmountToCalc) : Op()
    data class Multiply(val amount: AmountToCalc) : Op()
}

private sealed class AmountToCalc {
    data class ByNumber(val number: Int) : AmountToCalc()
    object ByOld : AmountToCalc()
}

private fun String.amountToCalc() = when (val parsed = this.toIntOrNull()) {
    null -> AmountToCalc.ByOld
    else -> AmountToCalc.ByNumber(parsed)
}

private fun List<String>.toOp() = when (component1()) {
    "*" -> Op.Multiply(component2().amountToCalc())
    "+" -> Op.Add(component2().amountToCalc())
    else -> error("No such op")
}

private data class Test(val divBy: Int, val pass: Int, val fail: Int)

private data class Monkey(
    val index: Int,
    val itemsWorryLevel: List<Int>,
    val op: Op,
    val test: Test,
    val inspections: Int = 0
)

private fun List<String>.toMonkey(index: Int): Monkey {
    val items = component2().substringAfter("Starting items:").split(", ").map { it.trim().toInt() }
    val op = component3().substringAfter("Operation: new = old ").split(" ").toOp()
    val divBy = component4().substringAfter("Test: divisible by ").toInt()
    val testTrue = component5().substringAfter("If true: throw to monkey ").toInt()
    val testFalse = this[5].substringAfter("If false: throw to monkey ").toInt()

    return Monkey(index, items, op, Test(divBy, testTrue, testFalse))
}

private fun parseInput(input: () -> String) = input().split("\n\r").mapIndexed { index, list ->
    list.trim().split("\n").map { it.trim() }.toMonkey(index)
}

// returns index of monkey to pass item to based on test result
private fun Int.performTest(test: Test) = when (this % test.divBy == 0) {
    true -> test.pass
    else -> test.fail
}

private fun Int.decreaseWorryLevel(divBy: Int) = floorDiv(divBy)
private fun Int.performOp(operation: Op) = when (operation) {
    is Op.Add -> when (operation.amount) {
        is AmountToCalc.ByNumber -> this + operation.amount.number
        AmountToCalc.ByOld -> this + this
    }

    is Op.Multiply -> when (operation.amount) {
        is AmountToCalc.ByNumber -> this * operation.amount.number
        AmountToCalc.ByOld -> this * this
    }
}

private fun monkeyBusiness(roundCount: Int, divideStressBy: Int): Int {
    val monkeys = parseInput { input }.toMutableList()

    repeat(roundCount) {
        repeat(monkeys.count()) { monkeyIndex ->
            val currentItemIterator = monkeys[monkeyIndex].itemsWorryLevel.toMutableList().listIterator()
            while (currentItemIterator.hasNext()) {
                val currMonkey = monkeys[monkeyIndex]
                val item = currentItemIterator.next()
                val newItem = item.performOp(currMonkey.op)
                    .decreaseWorryLevel(divideStressBy)
                val passTo = newItem.performTest(currMonkey.test)
                val passToMonkey = monkeys[passTo]
                val newPassToMonkey = passToMonkey.copy(itemsWorryLevel = passToMonkey.itemsWorryLevel + newItem)
                // update new monkey
                monkeys[passTo] = newPassToMonkey
                // remove item from inventory of old monkey
                // increase current monkey inspections
                val newCurrMonkey = currMonkey.copy(
                    itemsWorryLevel = currMonkey.itemsWorryLevel - item,
                    inspections = currMonkey.inspections + 1
                )
                monkeys[currMonkey.index] = newCurrMonkey
                // update the iterator at the end
                currentItemIterator.remove()
            }
        }
    }

    return monkeys.sortedByDescending { it.inspections }.take(2)
        .map { it.inspections }.reduce { acc, i -> acc * i }
}

private fun day111(): Int = monkeyBusiness(20, 3)

private fun day112(): Int = monkeyBusiness(1000, 1)


fun main() {
    println(day111())
    println(day111())
}