package day7

import java.io.File

sealed class Directory(
    open val name: String,
    open val files: MutableList<DirFile>,
    open val directories: MutableList<Directory>
) {
    data class Root(
        override val name: String,
        override val files: MutableList<DirFile> = mutableListOf(),
        override val directories: MutableList<Directory> = mutableListOf()
    ) : Directory(name, files, directories) {
        override fun toString(): String = "$name, ${calcSize()}"
    }

    data class Sub(
        override val name: String,
        override val files: MutableList<DirFile> = mutableListOf(),
        override val directories: MutableList<Directory> = mutableListOf(),
        val parent: Directory
    ) : Directory(name, files, directories) {
        override fun toString(): String = "$name, ${calcSize()}"
    }
}

data class DirFile(val size: Int, val name: String)

fun fileTreeParser(input: () -> List<String>): Directory.Root {
    val root = Directory.Root("/")
    var current: Directory = root

    fun cd(command: String) {
        current = when (command) {
            "/" -> root
            ".." -> (current as Directory.Sub).parent
            else -> current.findInCurrent(command)
        }
    }

    fun ls(ls: String) {
        val args = ls.split(' ')
        when {
            args.component1() == "dir" -> {
                val toAddName = args.component2()
                current.directories.add(Directory.Sub(name = toAddName, parent = current))
            }
            // is number
            args.component1().toIntOrNull() != null -> {
                val fileSize = args.component1()
                val fileName = args.component2()
                current.files.add(DirFile(fileSize.toInt(), fileName))
            }
            // theoretically not possible but still
            else -> error("No such command $ls")
        }
    }

    fun processInput(iterator: ListIterator<String>) {
        val next = iterator.next()
        val split = next.split(' ')
        when (split.component1()) {
            "$" -> when (split.component2()) {
                "ls" -> {
                    iterator.asSequence()
                        .forEach {
                            if (it.startsWith("$")) {
                                // reset the marker to the previous iteration
                                // as the startsWith check has advanced one line ahead to check,
                                // so we need to revert or one line will be skipped
                                iterator.previous()
                                return
                            }
                            ls(it)
                        }
                }

                "cd" -> cd(split.component3())
            }
        }
    }

    fun processCommands(commands: List<String>) {
        val iterator = commands.listIterator()
        while (iterator.hasNext()) {
            processInput(iterator)
        }
    }

    processCommands(input())
    return root
}

val root = fileTreeParser { File("src/main/kotlin/day7/Day7.txt").readLines() }

fun day71() = root
    .accumulateDirs().map { it.calcSize() }
    .filter { it < 100_000 }
    .sortedDescending()
    .sum()

const val TOTAL_SIZE = 70_000_000
const val NEEDED_SIZE = 30_000_000
fun day72(): Int {
    val neededSpace = NEEDED_SIZE - (TOTAL_SIZE - root.calcSize())
    return root.accumulateDirs()
        .map { it.name to it.calcSize() }
        .filter { it.second >= neededSpace }
        .minByOrNull { neededSpace }!!
        .second
}

fun main() {
    println(day71())
    println(day72())
}

fun Directory.accumulateDirs(): List<Directory> = this.directories.flatMap {
    it.accumulateDirs()
} + this.directories

fun Directory.calcSize(): Int = this.files.sumOf { it.size } + this.directories.sumOf { it.calcSize() }
fun Directory.findInCurrent(name: String) = this.directories.first { it.name == name }