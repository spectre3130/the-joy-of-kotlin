package org.kotlin.example.chap04

import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.stream.Stream

fun main() {
    val solution = Solution()
    val tailRecSolution = TailRecSolution()
//    println(solution.add(5, 10))
//    println(solution.add2(5, 10))
//    println(solution.factorial(2))
//    println(solution.sum(listOf(1,2,3,4,5)))
//    println(solution.sum2(listOf(1,2,3,4,5)))
//    println(solution.sumTail(listOf(1,2,3,4,5)))

    println(solution.fib(10))
    println(solution.fibonacci(10))
    println(solution.makeString(listOf("a", "b", "c"), ":"))

    println(tailRecSolution.sum(listOf(1,2,3,4,5)))
    println(tailRecSolution.makeString(listOf("a", "b", "c"), ":"))
    println(tailRecSolution.toString(listOf('a', 'b', 'c')))

    println(tailRecSolution.reverse(listOf('a', 'b', 'c')))

    println(tailRecSolution.range(0, 5))
}

fun <T> List<T>.head(): T =
    if (this.isEmpty())
        throw IllegalArgumentException("head called on empty list")
    else
        this[0]

fun <T> List<T>.tail(): List<T> =
    if(this.isEmpty())
        throw IllegalArgumentException("tail called on empty list")
    else
        this.drop(1)

class Solution {

    fun inc(n: Int) = n + 1
    fun dec(n: Int) = n - 1

    fun add(x: Int, y: Int): Int = if (y == 0) x else add(inc(x), dec(y))

    tailrec fun add2(x: Int, y: Int): Int {
        return if (y == 0) x else add2(inc(x), dec(y))
    }

    val factorial: (Int) -> Int by lazy { { n: Int ->
        if (n <= 1) n else n * factorial(n - 1)
    } }

    fun sum(list: List<Int>): Int {
        return if (list.isEmpty()) 0 else list[0] + sum(list.drop(1))
    }

    fun sum2(list: List<Int>): Int = if (list.isEmpty()) 0 else list.head() + sum(list.tail())

    fun sumTail(list: List<Int>): Int {
        tailrec fun sumTail(list: List<Int>, acc: Int): Int =
            if (list.isEmpty())
                acc
            else
                sumTail(list.tail(), acc + list.head())
        return sumTail(list, 0)
    }

    fun fib(x: Int): BigInteger {
        tailrec fun fib(val1: BigInteger, val2: BigInteger, x: BigInteger): BigInteger =
            when {
                (x == BigInteger.ZERO) -> BigInteger.ONE
                (x == BigInteger.ONE) -> val1 + val2
                else -> fib(val2, val1 + val2, x - BigInteger.ONE)
            }
        return fib(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(x.toLong())) //첫번째 인자가 ONE, 두번째인자가 ZERO로 시작 되어야 합니다.
    }

    fun fibonacci(n: Int): Int {
//        println(n)
        if (n <= 1) return n
        return fibonacci(n - 1) + fibonacci( n - 2)
    }

//    fun <T> makeString(list: List<T>, delim: String): String =
//        when {
//            list.isEmpty() -> ""
//            list.tail().isEmpty() -> "${list.head()}${makeString(list.tail(), delim)}"
//            else -> "${list.head()}$delim${makeString(list.tail(), delim)}"
//        }

    fun <T> makeString(list: List<T>, delim: String): String {
        tailrec fun makeString(list: List<T>, acc: String): String = when {
            list.isEmpty() -> acc
            acc.isEmpty() -> makeString(list.tail(), "${list.head()}")
            else -> makeString(list.tail(), "$acc$delim${list.head()}")
        }
        return makeString(list, "")
    }
}

class TailRecSolution {

    private fun <T, U> foldLeft(list: List<T>, z: U, f: (U, T) -> U): U {
        tailrec fun foldLeft(list: List<T>, acc: U): U =
            if (list.isEmpty()) acc
            else foldLeft(list.tail(), f(acc, list.head()))
        return foldLeft(list, z)

    }

    fun makeString(list: List<String>, delim: String): String {
        return foldLeft(list, "", { s, t -> if(s.isEmpty()) t else "$s$delim$t"} )
    }

    fun sum(list: List<Int>) = foldLeft(list, 0, Int::plus)

    private fun <T, U> foldRight(list: List<T>, identity: U, f: (T, U) -> U): U =
        if (list.isEmpty()) identity
        else f(list.head(), foldRight(list.tail(), identity, f))

    fun toString(list: List<Char>): String = foldRight(list, "") {c, s -> prepend(c, s) }

    private fun prepend(c: Char, s: String): String {
        println(c)
        return s + c
    }

    private fun <T> prepend(list: List<T>, elem: T): List<T> = listOf(elem) + list

    fun <T> reverse(list: List<T>): List<T> = foldLeft(list, listOf(), ::prepend)

//    fun range(start: Int, end: Int): List<Int> {
//        val result = mutableListOf<Int>()
//        var index = start
//        while (index < end) {
//            result.add(index)
//            index++
//        }
//        return result
//    }

    private fun <T> unfold(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> {
//        val result = mutableListOf<T>()
//        var elem = seed
//        while (p(elem)) {
//            result.add(elem)
//            elem = f(elem)
//        } // iteration
//        return result
//        if (p(seed))
//            prepend(unfold(f(seed), f, p), seed)
//        else
//            listOf() // recur
        tailrec fun unfold(acc: List<T>, seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> =
            if (p(seed))
                unfold(acc + seed, f(seed), f, p)
            else
                acc
        return unfold(listOf(), seed, f, p) // tail recur
    }

    fun range(start: Int, end: Int): List<Int> = unfold(start, { it + 1}, { it < end})

    fun <T> iterate(seed: T, f: (T) -> T, n: Int): List<T> {
        tailrec fun iterate_(acc: List<T>, seed: T): List<T> =
            if (acc.size < n)
                iterate_(acc + seed, f(seed))
            else
                acc
        return iterate_(listOf(), seed)
    }

    fun <T, U> map(list: List<T>, f: (T) -> U): List<U> {
        tailrec fun map(acc: List<U>, list: List<T>): List<U> =
            if (list.isEmpty())
                acc
            else
                map(acc + f(list.head()), list.tail())
        return map(listOf(), list)
    }
}